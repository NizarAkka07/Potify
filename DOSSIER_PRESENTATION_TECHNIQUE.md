# Dossier de Présentation Technique - Soutenance Avancement Potify

Ce document sert de support pour expliquer les choix d'ingénierie et la maturité technique du projet. L'objectif est de démontrer une maîtrise des concepts de microservices, de sécurité stateless et d'architecture hexagonale.

---

## 1. Vision Architecturale : Le découplage par l'Hexagone

**Argumentaire :**
"Au lieu de faire une application monolithique où tout est mélangé, j'ai opté pour une **Architecture Hexagonale (DDD)** sur le service `pool`. L'idée maîtresse est de protéger le 'Domain' (le métier) des technologies extérieures."

**Points clés à mettre en avant :**
*   **Isolation du Métier** : Le dossier `domain/` ne connaît ni Spring, ni PostgreSQL. Si on décide de changer de base de données, la logique de calcul des cagnottes reste intacte.
*   **Adaptateurs (Infrastructure)** : Tout ce qui est technique (REST, JPA) est relégué dans `infrastructure/`. C'est le principe de 'Plug & Play' appliqué au code.

---

## 2. Sécurité Stateless & JWT : Le lien avec les Tables SQL

**Argumentaire :**
"Nous avons implémenté une sécurité strictement **Stateless**. Le serveur n'a aucune mémoire des sessions utilisateurs, ce qui est le standard pour une architecture microservices scalable."

**Lien avec la base de données :**
*   **Phase d'Émission** : Lors du login, le service `user` interroge les tables **`users`**, **`roles`**, et **`permissions`**. Il compile ces droits dans un JWT (Badge numérique).
*   **Phase d'Action** : Quand l'utilisateur crée une ligne dans la table **`pools`**, le service `pool` n'a pas besoin de faire de JOIN avec la table des utilisateurs. Il extrait l'identité directement du JWT.
*   **Autonomie des services** : C'est ce qui permet au service `pool` de fonctionner même si le service `user` est temporairement indisponible (tant que le token est valide).

---

## 3. Résolution de Problématiques Complexes (Expertise Debug)

*C'est ici que tu montres ta valeur ajoutée. Un développeur junior subit les bugs, un développeur senior les explique et les anticipe.*

### A. Le conflit de Verrouillage Optimiste (Optimistic Locking)
"J'ai diagnostiqué et résolu une erreur `StaleObjectStateException`. Le problème venait d'une génération manuelle des UUID en Java qui entrait en conflit avec le cycle de vie de JPA. J'ai délégué la génération à Hibernate (`GenerationType.UUID`) pour garantir l'intégrité des transactions lors de la création des cagnottes."

### B. Synchronisation des Contraintes DB (Database Consistency)
"Pour assurer la fluidité de l'avancement malgré un schéma de base de données existant, j'ai implémenté un `CommandLineRunner` au démarrage du service `pool`. Il vient nettoyer les contraintes SQL obsolètes (`pools_status_check`) qui ne correspondaient plus aux nouvelles énumérations métier. Cela permet de garantir que le code et la base de données sont toujours synchronisés sans intervention manuelle."

---

## 4. Organisation de la Persistance : Le module `data-jpa`

**Argumentaire :**
"Pour éviter la duplication de code et les erreurs de mapping, j'ai centralisé la persistance dans un module Maven dédié : **`data-jpa`**."

**Bénéfices présentés :**
*   **Source unique de vérité** : Les entités comme `PoolEntity` ou `UserEntity` sont définies une seule fois.
*   **Cohérence du Schéma** : Tous les microservices voient exactement la même structure de table, ce qui élimine les bugs d'intégration lors des échanges de données.

---

## 5. Posture pour les Questions/Réponses

*   **Question : Pourquoi ne pas avoir tout mis dans une seule application ?**
    *   *Réponse* : "Pour la séparation des préoccupations. Le métier de la gestion des utilisateurs et celui de la gestion financière des cagnottes sont distincts. Les séparer nous permet de les déployer et de les faire évoluer indépendamment."
*   **Question : Le JWT n'est-il pas dangereux s'il est volé ?**
    *   *Réponse* : "C'est pour cela que nous utilisons des durées de vie courtes et que nous avons prévu les tables **`refresh_tokens`** et **`security_tokens`** pour gérer la révocation et le renouvellement sécurisé."

---

### Conclusion de la présentation :
"Le service est aujourd'hui stable, sécurisé et respecte les meilleures pratiques de l'industrie (SOLID, Stateless, Hexagonal). La fondation technique est prête pour accueillir les prochaines fonctionnalités complexes."
