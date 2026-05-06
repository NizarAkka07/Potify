# Guide Technique Approfondi : Architecture et Sécurité - Potify

Ce guide est destiné à expliquer en détail l'organisation du code, les choix architecturaux et les mécanismes de sécurité mis en place dans le projet Potify.

---

## 1. Microservice `pool` : L'Architecture Hexagonale (DDD)

Le microservice `pool` suit une architecture hexagonale stricte. L'objectif est d'isoler la logique métier (le "Cœur") des détails techniques (Base de données, API REST).

### Arborescence et Rôles des Fichiers

```text
pool/
├── src/main/java/com/alphateckplus/potify/pool/
│   ├── domain/                         <-- LE CŒUR (Pûr Java, aucune dépendance Spring)
│   │   ├── model/
│   │   │   ├── Pool.java               <-- Objet métier principal
│   │   │   ├── PoolType.java           <-- Enumération (PUBLIC, PRIVATE, etc.)
│   │   │   └── PoolStatus.java         <-- États (BROUILLON, PUBLIEE, etc.)
│   │   └── service/                    <-- Logique métier pure (règles de calcul)
│   │
│   ├── application_service/            <-- L'ORCHESTRATION (Cas d'utilisation)
│   │   ├── primary/                    <-- Interfaces d'entrée (Ports d'entrée)
│   │   │   └── pool/create_pool/
│   │   │       ├── CreatePoolService.java
│   │   │       └── DefaultCreatePoolService.java <-- Logique de création
│   │   └── secondary/                  <-- Interfaces de sortie (Ports de sortie)
│   │       └── pool/
│   │           └── PoolRepositoryPort.java  <-- Contrat pour la persistance
│   │
│   ├── infrastructure/                 <-- LA TECHNIQUE (Adaptateurs)
│   │   ├── primary/                    <-- Adaptateurs d'entrée (REST)
│   │   │   └── pool/
│   │   │       ├── dto/                <-- Objets d'échange JSON (CreatePoolRequest)
│   │   │       └── CreatePoolController.java <-- Point d'entrée API (/api/pools)
│   │   ├── secondary/                  <-- Adaptateurs de sortie (Persistance/JPA)
│   │   │   └── pool/
│   │   │       ├── mapper/             <-- MapStruct (Transforme Domaine <-> Entité)
│   │   │       └── repository/
│   │   │           └── PoolJpaAdapter.java  <-- Implémentation réelle de la sauvegarde
│   │   └── config/                     <-- Configuration Spring (Beans, Sécurité, CORS)
│   │       └── security/
│   │           └── SecurityConfig.java <-- Gestion des accès HTTP
│   │
│   └── PoolApplication.java            <-- Classe de démarrage (Main)
```

---

## 2. Microservice `user` : Architecture en Couches

Le service `user` utilise une structure plus classique mais robuste, centrée sur la gestion des identités.

### Arborescence et Rôles des Fichiers

```text
user/
├── src/main/java/com/alphateckplus/potify/user/
│   ├── application_service/            <-- Services applicatifs
│   │   └── primary/auth/
│   │       ├── login_user/             <-- Logique de connexion (LoginUserUseCase)
│   │       └── register_user/          <-- Logique d'inscription
│   ├── infrastructure/
│   │   ├── primary/auth/
│   │   │   ├── AuthController.java     <-- Endpoints /signup et /signin
│   │   │   └── dto/                    <-- AuthResponse (Contient maintenant l'ID)
│   │   ├── secondary/persistence/
│   │   │   └── DataInitializer.java    <-- Création automatique de l'admin au démarrage
│   │   └── config/security/
│   │       ├── JwtUtils.java           <-- Génération et validation des tokens JWT
│   │       └── WebSecurityConfig.java  <-- Configuration Spring Security (Filtres, Roles)
```

---

## 3. Le Module Central : `data-jpa`

Ce module est crucial car il est le **Point de Liaison** entre les microservices.

*   **Rôle** : Centraliser toutes les entités `@Entity` et les `@Repository`.
*   **Pourquoi ?** : Puisque les microservices partagent la même base de données PostgreSQL, ce module garantit qu'il n'y a pas de désynchronisation entre les tables.
*   **Fichiers Clés** :
    *   `PoolEntity.java` : Définition de la table `pools`.
    *   `UserEntity.java` : Définition de la table `users`.
    *   `ContributionEntity.java` : Table commune utilisée pour les paiements ET les participations aux cagnottes (Réutilisation de code).

---

## 4. Focus Sécurité : JWT et Permissions

### Sécurité Backend (Spring Security)
1.  **Authentification stateless** : Aucun état n'est gardé sur le serveur (pas de session). Tout est dans le JWT.
2.  **Rôles & Permissions** : 
    *   L'administrateur possède la permission `ALL`.
    *   Le système vérifie les permissions au niveau des méthodes (ou via les matchers de sécurité).
3.  **CORS (Cross-Origin Resource Sharing)** : 
    *   Configuration explicite pour autoriser le domaine du frontend (`localhost:9000`).
    *   Méthodes autorisées : `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`.

### Sécurité Frontend (Navigation & État)
1.  **Navigation Guards** (`src/router/index.js`) :
    *   Vérifie systématiquement le `localStorage.getItem('token')`.
    *   Si une route possède `meta: { requiresAuth: true }` et que le token est absent, redirection forcée vers `/login`.
2.  **Protection des Composants** :
    *   Utilisation du `authStore` (Vue 3 Reactive Store).
    *   Les éléments UI (ex: boutons d'édition) sont protégés par des `v-if="authStore.isAdmin"`.

---

## 5. Résumé des Correctifs Majeurs (Session du 03/05)

| Problème | Fichier Impacté | Solution Apportée |
| :--- | :--- | :--- |
| **Erreur 500 (ID Manquant)** | `AuthResponse.java` & `LoginPage.vue` | Ajout de l'UUID de l'utilisateur dans la réponse de login pour permettre l'association avec les cagnottes. |
| **Optimistic Locking** | `DefaultCreatePoolService.java` | Suppression de la génération manuelle d'ID. On laisse Hibernate gérer la génération UUID pour éviter les conflits de transaction. |
| **Contrainte SQL bloquante** | `PoolApplication.java` | Ajout d'un `CommandLineRunner` qui exécute `DROP CONSTRAINT` au démarrage pour nettoyer les anciennes contraintes de la table `pools`. |
| **Erreur d'import Frontend** | `CreatePoolPage.vue` | Correction des chemins d'import vers le store d'authentification (passage en chemins relatifs). |

---

**Note pour l'encadrant** : Cette architecture permet une séparation nette entre le métier (Domain) et la technique (Infrastructure), tout en assurant une sécurité robuste via JWT et une gestion unifiée de la base de données via le module `data-jpa`.
