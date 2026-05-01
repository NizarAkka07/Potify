# Explication detaillee du microservice user (Potify)

## 1. Objectif du microservice user

Le microservice user a pour role de gerer les utilisateurs de l'application Potify.
Il couvre actuellement les besoins suivants:

- creer un utilisateur
- recuperer un utilisateur par son identifiant
- lister les utilisateurs
- mettre a jour le statut d'un utilisateur

Ce service est construit avec une architecture hexagonale pour separer clairement:

- la logique metier (domaine)
- les cas d'usage (application_service)
- les details techniques (infrastructure)

Cette separation aide a respecter SOLID, a reduire le couplage, et a rendre le code plus facile a expliquer, tester et faire evoluer.

---

## 2. Vue d'ensemble de l'architecture

Le microservice suit le schema suivant:

- application_service
  - primary: ports d'entree (cas d'usage exposes a l'exterieur)
  - secondary: ports de sortie (abstractions vers la persistence)
- domain
  - model: objets metier purs
  - exception: exceptions metier explicites
- infrastructure
  - primary: adapter entrant REST (HTTP)
  - secondary: adapter sortant JPA (base de donnees)

### Pourquoi c'est hexagonal

- Les dependances vont de l'exterieur vers l'interieur.
- Le domaine ne depend pas de Spring, ni de JPA.
- Les couches techniques implementent des contrats (ports), elles ne dictent pas la logique metier.

---

## 3. Structure des dossiers (main)

Sous src/main/java/com/alphateckplus/potify/user:

- UserApplication.java
- application_service/
  - primary/
    - auth/
      - dto/
        - AuthResponse.java
        - LoginRequest.java
        - RegisterRequest.java
      - login_user/
        - LoginUserUseCase.java
      - register_user/
        - RegisterUserUseCase.java
    - command/
      - CreateUserCommand.java
      - UpdateUserCommand.java
      - AssignRoleToUserCommand.java
      - RemoveRoleFromUserCommand.java
      - CreateRoleCommand.java
      - UpdateRoleCommand.java
      - AssignPermissionToRoleCommand.java
      - RemovePermissionFromRoleCommand.java
      - CreatePermissionCommand.java
      - UpdatePermissionCommand.java
    - permission/
      - create_permission/ CreatePermissionService + DefaultCreatePermissionService
      - delete_permission/ DeletePermissionService + DefaultDeletePermissionService
      - get_permission_by_id/ GetPermissionByIdService + DefaultGetPermissionByIdService
      - list_permissions/ ListPermissionsService + DefaultListPermissionsService
      - update_permission/ UpdatePermissionService + DefaultUpdatePermissionService
    - role/
      - assign_permission_to_role/ AssignPermissionToRoleService + Default
      - create_role/ CreateRoleService + DefaultCreateRoleService
      - delete_role/ DeleteRoleService + DefaultDeleteRoleService
      - get_role_by_id/ GetRoleByIdService + DefaultGetRoleByIdService
      - list_roles/ ListRolesService + DefaultListRolesService
      - remove_permission_from_role/ RemovePermissionFromRoleService + Default
      - update_role/ UpdateRoleService + DefaultUpdateRoleService
    - user/
      - assign_role_to_user/ AssignRoleToUserService + Default
      - create_user/ CreateUserService + DefaultCreateUserService
      - get_user_by_id/ GetUserByIdService + DefaultGetUserByIdService
      - list_roles_by_user/ ListRolesByUserService + Default
      - list_users/ ListUsersService + DefaultListUsersService
      - remove_role_from_user/ RemoveRoleFromUserService + Default
      - update_user/ UpdateUserService + DefaultUpdateUserService
  - secondary/
    - permission/ PermissionRepositoryPort
    - role/ RoleRepositoryPort
    - user/ UserRepositoryPort
- domain/
  - model/
    - User.java
    - UserStatus.java
    - Role.java
    - Permission.java
  - exception/
    - UserAlreadyExistsException.java
    - UserNotFoundException.java
    - RoleAlreadyExistsException.java
    - RoleNotFoundException.java
    - PermissionAlreadyExistsException.java
    - PermissionNotFoundException.java
- infrastructure/
  - config/
    - UserBeanConfiguration.java
    - WebConfig.java
  - primary/
    - auth/
      - AuthController.java
    - permission/
      - add_permission/ AddPermissionController
      - delete_permission/ DeletePermissionController
      - get_all_permissions/ GetAllPermissionsController
      - get_permission_by_id/ GetPermissionByIdController
      - update_permission/ UpdatePermissionController
      - dto/ CreatePermissionRequest, UpdatePermissionRequest, PermissionResponse
      - mapper/ PermissionRestMapper
    - role/
      - add_role/ AddRoleController
      - assign_permission_to_role/ AssignPermissionToRoleController
      - delete_role/ DeleteRoleController
      - get_all_roles/ GetAllRolesController
      - get_role_by_id/ GetRoleByIdController
      - remove_permission_from_role/ RemovePermissionFromRoleController
      - update_role/ UpdateRoleController
      - dto/ CreateRoleRequest, UpdateRoleRequest, RoleResponse
      - mapper/ RoleRestMapper
    - security/
      - JwtAuthenticationFilter.java
      - JwtUtils.java
      - SecurityConfig.java
      - UserDetailsServiceImpl.java
    - user/
      - assign_role_to_user/ AssignRoleToUserController
      - get_roles_by_user/ GetRolesByUserController
      - remove_role_from_user/ RemoveRoleFromUserController
    - UserController.java
    - UserRestMapper.java
    - RestExceptionHandler.java
    - dto/ CreateUserRequest, UpdateUserRequest, UserResponse, ErrorResponse
  - secondary/
    - permission/
      - mapper/ PermissionPersistenceMapper
      - repository/ PermissionJpaAdapter
    - persistence/
      - DataInitializer.java
    - role/
      - mapper/ RolePersistenceMapper
      - repository/ RoleJpaAdapter
    - user/
      - UserJpaAdapter.java
      - UserPersistenceMapper.java

---

## 4. Role detaille de chaque couche

## 4.1 Couche domain

### User (agregat metier)

User represente l'utilisateur cote metier.
Il contient les attributs principaux:

- id
- fullName
- email
- password
- status
- createdAt
- updatedAt

Important:

- cette classe ne contient aucune annotation JPA
- cette classe ne depend pas de Spring
- elle reste testable sans infrastructure

### UserStatus

Enum metier:

- ACTIVE
- SUSPENDED
- DELETED

Cet enum evite les erreurs de saisie et formalise les etats metier autorises.

### Exceptions metier

- UserAlreadyExistsException: levee si un email existe deja
- UserNotFoundException: levee si l'utilisateur est introuvable

Ces exceptions rendent les regles metier explicites et lisibles.

---

## 4.2 Couche application_service

### Ports primary (entree)

Les interfaces de cas d'usage definissent ce que le systeme sait faire:

- CreateUserUseCase
- GetUserUseCase
- ListUsersUseCase
- UpdateUserStatusUseCase

Le controleur REST depend de ces interfaces, pas d'une classe concrete.

### Commands

Les commandes servent a transporter les donnees d'entree du cas d'usage.

- CreateUserCommand(fullName, email, password)
- UpdateUserStatusCommand(userId, status)

Avantage:

- contrat clair
- signatures de methodes simples
- extension facile

### Port secondary (sortie)

UserRepositoryPort definit les operations necessaires pour la persistence:

- save
- findById
- findByEmail
- findAll
- existsByEmail

La logique metier ne connait que ce port, pas JPA ni la base reelle.

### UserService (coeur des cas d'usage)

UserService implemente tous les ports primary.

Regles principales:

1. create user
- verifie l'unicite de l'email via existsByEmail
- si email deja pris, leve UserAlreadyExistsException
- construit un User avec status ACTIVE
- sauvegarde via UserRepositoryPort

2. get user by id
- lit par id
- si absent, leve UserNotFoundException

3. list users
- retourne la liste depuis le repository

4. update status
- lit l'utilisateur par id
- si absent, leve UserNotFoundException
- met a jour le status
- sauvegarde

191: Transactions:

- classe en readOnly=true par defaut
- create et update en @Transactional (ecriture)

---

## 4.3 Couche infrastructure primary (REST)

### UserController

Expose l'API HTTP sous:

- /api/v1/users

Endpoints:

1. POST /api/v1/users
- entree: CreateUserRequest
- validation automatique (@Valid)
- appelle CreateUserUseCase
- retourne 201 Created + Location

2. GET /api/v1/users/{userId}
- appelle GetUserUseCase
- retourne 200 OK

3. GET /api/v1/users
- appelle ListUsersUseCase
- retourne 200 OK

4. PATCH /api/v1/users/{userId}/status
- entree: UpdateUserStatusRequest
- appelle UpdateUserStatusUseCase
- retourne 200 OK

### DTO d'entree

CreateUserRequest impose:

- fullName obligatoire et max 120
- email obligatoire, format email, max 150
- password obligatoire, min 6, max 255

UpdateUserStatusRequest impose:

- status obligatoire

### DTO de sortie

UserResponse expose uniquement les champs utiles au client.

Le mot de passe n'est pas retourne.

### UserRestMapper

Convertit User (domaine) vers UserResponse (contrat HTTP).

Cela evite d'exposer directement le modele domaine sur le reseau.

### RestExceptionHandler

Centralise la conversion des exceptions en reponses HTTP coherentes:

- UserNotFoundException -> 404
- UserAlreadyExistsException -> 409
- MethodArgumentNotValidException -> 400
- Exception generique -> 500

Format commun de reponse d'erreur:

- timestamp
- status
- error
- message
- path

---

## 4.4 Couche infrastructure secondary (persistence)

### UserJpaAdapter

Implemente UserRepositoryPort en utilisant Spring Data JPA.

Dependances:

- UserEntityRepository (module data-jpa)
- UserPersistenceMapper

Role:

- convertir domaine -> entite JPA avant save
- convertir entite JPA -> domaine apres lecture

### UserPersistenceMapper

Assure la conversion:

- User <-> UserEntity
- UserStatus (domaine) <-> UserStatus (data-jpa)

Ce mapper est important pour garder l'independance du domaine.

---

## 4.5 Couche domaine etendue: Role et Permission

### Role (modele metier)

Role represente un groupe de permissions attribuable a un utilisateur.

Attributs:

- id
- name
- description
- permissions (Set<Permission>)

Important:

- Pas d'annotation JPA ni Spring dans cette classe
- Contient une collection de permissions directement en memoire domaine

### Permission (modele metier)

Permission represente un droit precis dans le systeme.

Attributs:

- id
- code (ex: ALL, READ_USERS)
- description

Utilisee comme unite granulaire d'acces pour construire les roles.

### Nouvelles exceptions metier

- RoleAlreadyExistsException: levee si un role avec ce nom existe deja
- RoleNotFoundException: levee si le role est introuvable par id ou nom
- PermissionAlreadyExistsException: levee si un code de permission est deja pris
- PermissionNotFoundException: levee si la permission est introuvable

---

## 4.6 Couche application_service etendue: Auth, Role, Permission

### Ports primary auth

#### LoginUserUseCase

Gere la connexion d'un utilisateur:

1. Cherche l'utilisateur par email via UserRepositoryPort
2. Verifie si le compte est verrouille (accountNonLocked)
3. Si verrouillage expire, reinitialise les tentatives
4. Delegue l'authentification a Spring AuthenticationManager
5. En cas d'echec, incremente failedAttempts et sauvegarde
6. En cas de succes, reinitialise failedAttempts
7. Genere un JWT via JwtUtils et retourne AuthResponse

Note: le refreshToken est encore un mock. A implementer en base via SecurityTokenEntity.

#### RegisterUserUseCase

Gere l'inscription d'un nouveau compte:

1. Verifie si l'email est deja utilise
2. Encode le mot de passe via PasswordEncoder (BCrypt)
3. Construit un User avec status ACTIVE, enabled=true, accountNonLocked=true
4. Sauvegarde via UserRepositoryPort

TODO: generer un SecurityToken pour verification email.

### DTO d'authentification

- LoginRequest: email + password
- RegisterRequest: fullName + email + password
- AuthResponse: accessToken + refreshToken

Ces DTO sont places dans application_service/primary/auth/dto pour rester accessibles
au cas d'usage sans traverser l'infrastructure.

### Ports primary user (nouveaux)

Chaque cas d'usage a son interface + son implementation Default:

- CreateUserService / DefaultCreateUserService: creation avec unicite email
- GetUserByIdService / DefaultGetUserByIdService: lecture par id
- ListUsersService / DefaultListUsersService: liste tous les users
- UpdateUserService / DefaultUpdateUserService: mise a jour fullName/email
- AssignRoleToUserService / DefaultAssignRoleToUserService: attribue un role
- RemoveRoleFromUserService / DefaultRemoveRoleFromUserService: retire un role
- ListRolesByUserService / DefaultListRolesByUserService: liste les roles d'un user

### Ports primary role

- CreateRoleService / DefaultCreateRoleService
- GetRoleByIdService / DefaultGetRoleByIdService
- ListRolesService / DefaultListRolesService
- UpdateRoleService / DefaultUpdateRoleService
- DeleteRoleService / DefaultDeleteRoleService
- AssignPermissionToRoleService / DefaultAssignPermissionToRoleService
- RemovePermissionFromRoleService / DefaultRemovePermissionFromRoleService

### Ports primary permission

- CreatePermissionService / DefaultCreatePermissionService
- GetPermissionByIdService / DefaultGetPermissionByIdService
- ListPermissionsService / DefaultListPermissionsService
- UpdatePermissionService / DefaultUpdatePermissionService
- DeletePermissionService / DefaultDeletePermissionService

Chaque service (Default) recoit les ports secondaires via constructeur.
Aucune annotation @Service ou @Autowired dans ces classes: le wiring est fait par UserBeanConfiguration.

### Ports secondary (sortie) nouveaux

#### PermissionRepositoryPort

Operations:

- save
- findById
- findByCode
- findAll
- deleteById

#### RoleRepositoryPort

Operations:

- save
- findById
- findByName
- findAll
- deleteById
- addPermissionToRole
- removePermissionFromRole

Ces ports gardent le metier independant de JPA.

---

## 4.7 Couche infrastructure primary: nouveaux controleurs

### AuthController

Exposes sous /api/auth (public, sans token):

- POST /api/auth/signup -> RegisterUserUseCase
- POST /api/auth/signin -> LoginUserUseCase -> retourne AuthResponse avec JWT

Ces endpoints sont exclus du filtre JWT (voir JwtAuthenticationFilter).

### Controleurs permission

Tous sous /api/v1/access/permissions (ADMIN requis):

- AddPermissionController: POST -> cree une permission
- GetAllPermissionsController: GET -> liste toutes
- GetPermissionByIdController: GET /{id} -> retourne une permission
- UpdatePermissionController: PUT /{id} -> modifie code/description
- DeletePermissionController: DELETE /{id} -> supprime

#### DTO permission

- CreatePermissionRequest: code (obligatoire), description
- UpdatePermissionRequest: code, description
- PermissionResponse: id, code, description

#### PermissionRestMapper

Convertit entre PermissionRequest, Permission (domaine) et PermissionResponse.

### Controleurs role

Tous sous /api/v1/access/roles (ADMIN requis):

- AddRoleController: POST -> cree un role
- GetAllRolesController: GET -> liste tous
- GetRoleByIdController: GET /{id}
- UpdateRoleController: PUT /{id}
- DeleteRoleController: DELETE /{id}
- AssignPermissionToRoleController: POST /{roleId}/permissions/{permissionId}
- RemovePermissionFromRoleController: DELETE /{roleId}/permissions/{permissionId}

#### RoleRestMapper

Convertit entre RoleRequest, Role (domaine) et RoleResponse.

### Controleurs user (gestion des roles)

Sous /api/v1/access/users (ADMIN requis):

- AssignRoleToUserController: POST /{userId}/roles/{roleId}
- RemoveRoleFromUserController: DELETE /{userId}/roles/{roleId}
- GetRolesByUserController: GET /{userId}/roles

---

## 4.8 Couche infrastructure primary: securite JWT

### JwtUtils

Utilitaire de gestion des tokens JWT:

- secretKey et jwtExpiration lus depuis application.yml
- generateToken(UserDetails): cree un token signe HMAC-SHA
- extractUsername(token): lit le subject (email) depuis le token
- isTokenValid(token, userDetails): verifie username + expiration
- Cle secrete derivee via Keys.hmacShaKeyFor

### JwtAuthenticationFilter

Filtre Spring Security execute une fois par requete (OncePerRequestFilter):

1. Laisse passer /api/auth/** sans verification
2. Lit l'en-tete Authorization
3. Si Bearer token present, extrait l'email
4. Charge le UserDetails via UserDetailsService
5. Valide le token
6. Injecte l'Authentication dans le SecurityContext

### UserDetailsServiceImpl

Implemente UserDetailsService de Spring Security:

- Charge l'utilisateur par email via UserRepositoryPort
- Mappe les roles domaine en GrantedAuthority Spring (prefixe ROLE_)
- Gere l'etat enabled et accountNonLocked
- Fait le pont entre le domaine metier et le systeme de securite Spring

### SecurityConfig

Configuration globale Spring Security:

- CSRF desactive (API REST stateless)
- Sessions stateless (SessionCreationPolicy.STATELESS)
- Endpoints publics: /api/auth/**, Swagger UI, OpenAPI
- /api/v1/** protege: requiert le role ADMIN
- BCryptPasswordEncoder pour le hashage des mots de passe
- DaoAuthenticationProvider avec UserDetailsServiceImpl
- CORS configure pour http://localhost:9000 (frontend Quasar)

---

## 4.9 Couche infrastructure secondary: nouveaux adapters

### PermissionJpaAdapter

Implemente PermissionRepositoryPort via Spring Data JPA:

- Utilise PermissionEntityRepository (data-jpa)
- Utilise PermissionPersistenceMapper pour les conversions domaine <-> entite
- Operations: save, findById, findByCode, findAll, deleteById

### PermissionPersistenceMapper

Convertit Permission (domaine) <-> PermissionEntity (JPA).

### RoleJpaAdapter

Implemente RoleRepositoryPort via Spring Data JPA:

- Utilise RoleEntityRepository et PermissionEntityRepository
- Lors du save avec id existant: met a jour sans ecraser les permissions (logique protegee)
- addPermissionToRole: charge role et permission, ajoute a la collection JPA et sauvegarde
- removePermissionFromRole: retire la permission de la collection et sauvegarde

### RolePersistenceMapper

Convertit Role (domaine) <-> RoleEntity (JPA).
Utilise PermissionPersistenceMapper pour convertir les permissions imbriquees.

### DataInitializer

Composant CommandLineRunner execute au demarrage:

1. Verifie si admin@potify.com existe deja en base
2. Si absent:
   - Cree la permission ALL (acces complet)
   - Cree le role ADMIN avec la permission ALL
   - Cree l'utilisateur admin@potify.com / admin123 (mot de passe encode BCrypt)
   - Statut ACTIVE, enabled=true, accountNonLocked=true
3. Affiche des logs de diagnostic au demarrage

Objectif: garantir qu'un compte administrateur existe toujours au premier demarrage.

---

## 4.10 Couche infrastructure config

### UserBeanConfiguration

Classe @Configuration qui centralise tous les beans de l'application:

- Declare les mappers: PermissionPersistenceMapper, RolePersistenceMapper, UserPersistenceMapper
- Declare les ports secondaires: PermissionJpaAdapter, RoleJpaAdapter, UserJpaAdapter
- Declare tous les services (cas d'usage): un bean par service Default
- Injecte les dependances via constructeur (pas d'@Autowired dans les classes metier)

Pourquoi cette approche:

- Les classes DefaultXxxService n'ont aucune annotation Spring
- Elles restent pures et testables sans contexte Spring
- Le wiring est entierement visible et centralise dans un seul fichier
- Respecte le principe D de SOLID: le domaine depend d'abstractions, pas de Spring

### WebConfig

Configuration CORS MVC complementaire a SecurityConfig:

- Autorise les origines localhost:9000, 9001, 9002 (ports Quasar dev)
- Methodes autorisees: GET, POST, PUT, DELETE, OPTIONS, PATCH
- Headers et credentials autorises

---

## 5. Bootstrapping et integration data-jpa

### UserApplication

Annotations clefs:

- @SpringBootApplication
- @EnableJpaAuditing
- @EnableJpaRepositories(basePackages = com.alphateckplus.potify.data_jpa.repository)
- @EntityScan(basePackages = com.alphateckplus.potify.data_jpa.entity)

Explication:

Le module user reutilise les entites et repositories du module data-jpa.
Il faut donc indiquer explicitement a Spring ou scanner ces packages.

---

## 6. Configuration technique

Fichier: src/main/resources/application.yml

Points importants:

- application name: user
- base H2 en memoire
- mode PostgreSQL pour compatibilite SQL locale
- hibernate ddl-auto: update
- h2 console activee
- port serveur: 8081

Objectif:

- demarrage local rapide
- tests et developpement facilites

---

## 7. Respect des principes SOLID

### S - Single Responsibility Principle

Chaque composant a une seule responsabilite:

- UserService: orchestration metier
- UserController: transport HTTP
- UserJpaAdapter: acces persistence
- mappers: transformations de donnees

### O - Open/Closed Principle

On peut ajouter de nouveaux cas d'usage sans casser les existants.
Par exemple un DeleteUserUseCase peut etre ajoute proprement.

### L - Liskov Substitution Principle

Les implementations des ports respectent leurs contrats.
Le service peut utiliser n'importe quelle implementation de UserRepositoryPort.

### I - Interface Segregation Principle

Ports metier cibles et lisibles, pas de grosse interface monolithique exposee aux clients.

### D - Dependency Inversion Principle

Le coeur metier depend d'abstractions (ports) et non des details (JPA, HTTP).

---

## 8. Couplage faible: comment il est obtenu

1. Domaine sans annotation technique
2. Service depend de UserRepositoryPort
3. Adaptateurs se branchent autour du domaine
4. DTO REST separes des modeles domaine
5. Mapper dedie pour isoler les conversions

Resultat:

- code plus testable
- maintenance plus facile
- remplacement de la techno persistence possible sans toucher au metier

---

## 9. Scenarios metier a expliquer a l'oral

### Scenario A: creation d'utilisateur

1. Le client envoie POST /api/v1/users
2. Validation de CreateUserRequest
3. UserController construit CreateUserCommand
4. UserService verifie unicite email
5. UserService construit User avec status ACTIVE
6. UserJpaAdapter sauvegarde via UserEntityRepository
7. Mapper reconvertit vers User domaine
8. Controller retourne 201 + UserResponse

### Scenario B: mise a jour de statut

1. Client envoie PATCH /api/v1/users/{id}/status
2. Validation du status
3. Service lit user par id
4. Si absent -> UserNotFoundException -> 404
5. Sinon, mise a jour status + save
6. Retour 200 avec user mis a jour

### Scenario C: erreur de validation

1. Requete invalide (email mauvais format, champ vide, etc.)
2. Spring leve MethodArgumentNotValidException
3. RestExceptionHandler produit une reponse 400 avec message lisible

---

## 10. Strategie de tests actuelle

Test principal:

- UserServiceTest (tests unitaires avec Mockito)

Ce test valide:

- rejet si email deja existant
- creation avec status ACTIVE
- rejet update status si user absent

Avantage:

- tests rapides
- verifies sur la logique metier pure
- pas besoin de demarrer tout Spring pour chaque test

---

## 11. Limites actuelles et ameliorations proposees

Points actuels:

- password stocke en clair dans le domaine et persistance
- pas encore de gestion de roles/permissions cote service user
- pas encore d'API de suppression logique

Ameliorations conseillees:

1. integrer un PasswordEncoder (BCrypt)
2. ajouter tests d'integration REST (MockMvc)
3. ajouter pagination sur GET /users
4. ajouter endpoint de desactivation/reactivation utilisateur
5. journaliser les actions critiques (audit metier)

---

## 12. Script oral simple pour soutenance

Tu peux presenter en 5 temps:

1. Contexte
- microservice user dans un monorepo modulaire
- architecture hexagonale pour isoler le metier

2. Architecture
- domain au centre
- application_service pour les cas d'usage
- infrastructure pour HTTP/JPA

3. Exemple concret
- parcours complet de POST /api/v1/users
- validation, commande, service, port, adapter, reponse

4. Qualite logicielle
- SOLID applique
- couplage faible
- test unitaire service

5. Evolution
- hash mot de passe
- tests integration
- nouvelles fonctionnalites sans casser l'existant

---

## 13. Conclusion

Le microservice user est structure pour etre:

- lisible
- pedagogique
- testable
- evolutif

Il est adapte a une presentation academe car chaque couche a un role net et chaque decision technique peut etre justifiee facilement devant un encadrant.
