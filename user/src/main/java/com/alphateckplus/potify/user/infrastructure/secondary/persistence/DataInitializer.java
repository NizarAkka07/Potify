package com.alphateckplus.potify.user.infrastructure.secondary.persistence;

// Importation des entités de persistance du module data-jpa
import com.alphateckplus.potify.data_jpa.entity.user.PermissionEntity;
import com.alphateckplus.potify.data_jpa.entity.user.RoleEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserStatus;
// Importation des repositories pour l'accès direct à la base de données lors de l'initialisation
import com.alphateckplus.potify.data_jpa.repository.user.PermissionEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.RoleEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
// Importation des utilitaires Java et Spring
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Composant d'initialisation des données (DataInitializer).
 * Ce composant implémente CommandLineRunner pour s'exécuter automatiquement au démarrage de l'application.
 * Son rôle est de garantir qu'un utilisateur administrateur par défaut existe en base de données.
 */
@Component // Indique à Spring de détecter ce composant automatiquement
@RequiredArgsConstructor // Génère le constructeur pour l'injection des dépendances final
public class DataInitializer implements CommandLineRunner {

    // Dépendances vers les repositories et l'encodeur de mot de passe
    private final UserEntityRepository userRepository;
    private final RoleEntityRepository roleRepository;
    private final PermissionEntityRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Méthode exécutée au démarrage.
     * @param args Arguments de la ligne de commande (non utilisés ici).
     */
    @Override
    @Transactional // Garantit que toute l'initialisation se fait dans une seule transaction atomique
    public void run(String... args) {
        // Vérification du nombre d'utilisateurs actuels
        long userCount = userRepository.count();
        System.out.println(">>> [Initialisation] Nombre d'utilisateurs en base : " + userCount);
        
        // Si l'administrateur par défaut existe déjà, on ne fait rien
        if (userRepository.findByEmail("admin@potify.com").isPresent()) {
            System.out.println(">>> [Initialisation] L'utilisateur admin@potify.com existe déjà. Passage.");
            return;
        }

        System.out.println(">>> [Initialisation] Base de données vide ou admin absent. Création de l'administrateur par défaut...");

        // 1. Création de la permission 'ALL' (Accès total)
        // Respecte le principe de granularité des accès
        PermissionEntity allPermission = PermissionEntity.builder()
                .code("ALL")
                .description("Accès complet à toutes les ressources")
                .build();
        permissionRepository.save(allPermission); // Sauvegarde en base

        // 2. Création du rôle 'ADMIN'
        // On associe la permission 'ALL' à ce rôle
        RoleEntity adminRole = RoleEntity.builder()
                .name("ADMIN")
                .description("Administrateur du système")
                .permissions(Set.of(allPermission)) // Association de la permission
                .build();
        roleRepository.save(adminRole); // Sauvegarde du rôle

        // 3. Création de l'utilisateur 'ADMIN'
        // Cet utilisateur aura le rôle ADMIN et pourra se connecter immédiatement
        UserEntity adminUser = UserEntity.builder()
                .fullName("Admin System")
                .email("admin@potify.com")
                // Encodage sécurisé du mot de passe (admin123)
                .password(passwordEncoder.encode("admin123"))
                .status(UserStatus.ACTIVE)
                .enabled(true) // Compte activé
                .accountNonLocked(true) // Compte non verrouillé
                .roles(Set.of(adminRole)) // Attribution du rôle
                .build();
        userRepository.save(adminUser); // Sauvegarde de l'utilisateur

        System.out.println(">>> [Initialisation] Données d'administration créées : admin@potify.com / admin123");
    }
}
