# Rapport d'Intégration : Cloudinary (Médias) & SSE (Temps Réel)

Ce document présente le bilan technique complet et l'explication détaillée de l'intégration de **Cloudinary** (pour le stockage cloud sécurisé des vidéos sans erreur CORS) et du protocole **SSE (Server-Sent Events)** (pour les commentaires et les réactions en temps réel).

---

## ☁️ Part I : Intégration de Cloudinary (Stockage Hybride Sécurisé)

Auparavant, l'application tentait d'uploader la vidéo directement du navigateur vers Cloudinary (ce qui déclenchait des **erreurs CORS** en raison de l'absence de configuration d'origine) ou d'envoyer la vidéo en Base64 dans la requête de création de cagnotte (ce qui causait une erreur **413 Payload Too Large** car le serveur Spring Boot n'est pas conçu pour recevoir des requêtes JSON gigantesques).

### 🛠️ Architecture du Flux d'Upload
```
[Navigateur Frontend] 
       │ 
       ▼ (1) POST /api/pools (Metadata JSON sans vidéo)
[Spring Boot Backend] ──► Crée la cagnotte & retourne l'ID
       │
       ▼ (2) POST /api/pools/{id}/video (FormData avec fichier vidéo brut - ZÉRO CORS)
[Spring Boot Backend]
       │
       ▼ (3) Téléversement via SDK Cloudinary (Requête Serveur-à-Serveur - ZÉRO CORS)
[Cloudinary Cloud]
       │
       ▼ (4) Retourne l'URL Cloud sécurisée (https://res.cloudinary.com/...)
[Spring Boot Backend] ──► Met à jour 'video_url' dans PostgreSQL & vide le stockage local (0 octet local !)
```

### 📂 Fichiers et Explications du Code

#### 1. Dépendance Maven
*   **Fichier** : [`pool/pom.xml`](file:///c:/Users/nizar/Desktop/Project/potify/pool/pom.xml)
*   **Action** : Ajout du SDK officiel de Cloudinary pour Java :
    ```xml
    <dependency>
        <groupId>com.cloudinary</groupId>
        <artifactId>cloudinary-http5</artifactId>
        <version>2.0.0</version>
    </dependency>
    ```

#### 2. Configuration de l'Environnement
*   **Fichier** : [`pool/src/main/resources/application.yml`](file:///c:/Users/nizar/Desktop/Project/potify/pool/src/main/resources/application.yml)
*   **Action** : Configuration de vos accès réels pour authentifier le serveur auprès de Cloudinary :
    ```yaml
    application:
      cloudinary:
        cloud-name: dsoliwbee
        api-key: "628423652634248"
        api-secret: wL8TV51jdidtGpCP7ibcn4pFgNA
    ```

#### 3. Service d'Upload Dédié
*   **Fichier** : [`CloudinaryService.java`](file:///c:/Users/nizar/Desktop/Project/potify/pool/src/main/java/com/alphateckplus/potify/pool/infrastructure/secondary/cloudinary/CloudinaryService.java)
*   **Explication** : Ce service Spring initialise l'instance `Cloudinary` avec vos credentials et fournit une méthode pour pousser les fichiers sur votre Cloud :
    ```java
    public String uploadVideo(MultipartFile file) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "video"
        ));
        return (String) uploadResult.get("secure_url");
    }
    ```

#### 4. Le Contrôleur d'API Vidéo
*   **Fichier** : [`PoolVideoController.java`](file:///c:/Users/nizar/Desktop/Project/potify/pool/src/main/java/com/alphateckplus/potify/pool/infrastructure/primary/pool/PoolVideoController.java)
*   **Explication** : Il écoute les requêtes multipart. Il intercepte le fichier, le délègue à Cloudinary, puis met à jour la table PostgreSQL avec l'URL finale tout en s'assurant de ne pas stocker de gros paquets de `byte[]` en local pour ne pas polluer l'espace de votre base de données locale.

#### 5. Orchestration Côté Frontend
*   **Fichier** : [`CreatePoolPage.vue`](file:///c:/Users/nizar/Desktop/Project/potify/frontend/src/pool/pages/CreatePoolPage.vue)
*   **Explication** : La méthode d'envoi a été découpée de manière asynchrone :
    1. Envoi des métadonnées textuelles de la cagnotte.
    2. Si une vidéo locale est présente, création d'un `FormData` pour pousser le fichier physique brute vers le backend local.

---

## ⚡ Part II : Protocole SSE (Server-Sent Events) pour le Temps Réel

Le protocole **SSE** est une alternative légère aux WebSockets. Il permet au serveur d'envoyer des notifications à sens unique vers le client (Push) de manière native via une simple connexion HTTP persistante, idéale pour un fil d'actualité de commentaires.

### 🛠️ Architecture du Temps Réel
```
[Navigateur Frontend] (Souscrit au flux) ──► GET /api/messages/pool/{id}/stream (Connexion HTTP persistante ouverte)
       ▲
       │ (Le serveur garde le canal ouvert pour ce client via un SseEmitter)
       │
[Autre Utilisateur] ──► POST /api/messages (Ajoute un commentaire ou clique sur une réaction)
       │
       ▼ (Le backend intercepte l'événement)
[MessageController] ──► Appelle PoolSseService.broadcastMessage()
       │
       ▼ (Parcourt tous les SseEmitters connectés à cette cagnotte)
[PoolSseService] ──► Pousse le JSON du commentaire en direct via SSE
       │
       ▼ (Événement 'message' reçu instantanément)
[Lecteur Frontend (EventSource)] ──► Met à jour la liste des messages en direct sur l'écran
```

### 📂 Fichiers et Explications du Code

#### 1. Le Gestionnaire de Flux Backend
*   **Fichier** : [`PoolSseService.java`](file:///c:/Users/nizar/Desktop/Project/potify/pool/src/main/java/com/alphateckplus/potify/pool/infrastructure/primary/pool/message/PoolSseService.java)
*   **Explication** : Ce service gère les connexions actives des utilisateurs dans une Map thread-safe (`ConcurrentHashMap`) et gère le nettoyage lors des déconnexions :
    ```java
    // Map qui associe chaque ID de cagnotte à la liste des clients actifs (SseEmitter)
    private final Map<String, List<SseEmitter>> poolEmitters = new ConcurrentHashMap<>();

    // Souscription à un flux
    public SseEmitter subscribe(String poolId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        poolEmitters.computeIfAbsent(poolId, k -> new ArrayList<>()).add(emitter);
        
        // Nettoyage en cas de déconnexion ou timeout
        emitter.onCompletion(() -> removeEmitter(poolId, emitter));
        emitter.onTimeout(() -> removeEmitter(poolId, emitter));
        emitter.onError((e) -> removeEmitter(poolId, emitter));
        return emitter;
    }
    ```

#### 2. La Diffusion (Broadcast) des Événements
Lorsqu'un message est créé ou qu'un utilisateur clique sur une réaction, le `MessageController` appelle la diffusion :
```java
public void broadcastMessage(String poolId, Message message) {
    List<SseEmitter> emitters = poolEmitters.get(poolId);
    if (emitters != null) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                // Envoi de l'événement SSE "message" avec le JSON du commentaire
                emitter.send(SseEmitter.event().name("message").data(message));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters); // Supprime les clients déconnectés
    }
}
```

#### 3. L'Exposition de l'API (Produit du Text/Event-Stream)
*   **Fichier** : [`MessageController.java`](file:///c:/Users/nizar/Desktop/Project/potify/pool/src/main/java/com/alphateckplus/potify/pool/infrastructure/primary/pool/message/MessageController.java)
*   **Explication** : L'endpoint de streaming spécifie le type MIME `text/event-stream` :
    ```java
    @GetMapping(value = "/pool/{poolId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPoolMessages(@PathVariable String poolId) {
        return sseService.subscribe(poolId);
    }
    ```

#### 4. Réception réactive dans le Frontend
*   **Fichier** : [`PoolDetailPage.vue`](file:///c:/Users/nizar/Desktop/Project/potify/frontend/src/pool/pages/PoolDetailPage.vue)
*   **Explication** : Le composant utilise l'API standard JavaScript `EventSource` pour se connecter au flux. 
    Lorsqu'un événement arrive :
    - Si le message existe déjà dans l'affichage (ex: l'utilisateur a modifié une réaction), le frontend remplace le message existant avec les nouvelles données.
    - S'il s'agit d'un nouveau commentaire, il l'insère instantanément en haut de la liste (`unshift`).
    ```javascript
    const setupMessageSSE = () => {
      const url = `http://localhost:8082/api/messages/pool/${route.params.id}/stream`
      messageEventSource = new EventSource(url)

      messageEventSource.addEventListener('message', (event) => {
        const newMessage = JSON.parse(event.data)
        const index = messages.value.findIndex(m => m.id === newMessage.id)
        if (index !== -1) {
          // Mise à jour réactive (nouvelle réaction cliquée par un autre utilisateur)
          messages.value[index] = newMessage
        } else {
          // Nouveau commentaire poussé par un autre utilisateur
          messages.value.unshift(newMessage)
        }
      })
    }
    ```
    - Lors de la fermeture de la page (onUnmount), la connexion est coupée proprement (`messageEventSource.close()`) pour libérer les ressources du serveur.

---

### 💡 Bilan de l'Architecture
- **Cloudinary** gère le stockage cloud lourd sans les désagréments de sécurité du CORS navigateur.
- **PostgreSQL** gère les relations de données structurées et légères.
- **SSE** fournit une mise à jour en temps réel légère, automatique et réactive à vos utilisateurs sans consommer de bande passante inutile (pas de requêtes HTTP répétées en boucle ou "polling").
