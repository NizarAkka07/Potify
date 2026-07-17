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
import java.util.HashSet;
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
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    /**
     * Méthode exécutée au démarrage.
     * @param args Arguments de la ligne de commande (non utilisés ici).
     */
    @Override
    @Transactional // Garantit que toute l'initialisation se fait dans une seule transaction atomique
    public void run(String... args) {
        // Nettoyage des contraintes obsolètes (Hibernate génère des CHECK constraints rigides)
        try {
            System.out.println(">>> [Nettoyage] Suppression de la contrainte users_status_check...");
            jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_status_check");
            System.out.println(">>> [Nettoyage] Contrainte supprimée avec succès.");
        } catch (Exception e) {
            System.out.println(">>> [Nettoyage] Info: Pas de contrainte à supprimer ou erreur : " + e.getMessage());
        }

        // Vérification du nombre d'utilisateurs actuels
        long userCount = userRepository.count();
        System.out.println(">>> [Initialisation] Nombre d'utilisateurs en base : " + userCount);
        
        // 1. Initialiser les permissions si elles n'existent pas
        PermissionEntity allPerm = getOrCreatePermission("ALL", "Accès complet à toutes les ressources");
        PermissionEntity manageUsersPerm = getOrCreatePermission("MANAGE_USERS", "Gérer les utilisateurs");
        PermissionEntity manageRolesPerm = getOrCreatePermission("MANAGE_ROLES", "Gérer les rôles et permissions");
        PermissionEntity validatePoolsPerm = getOrCreatePermission("VALIDATE_POOLS", "Valider et gérer les cagnottes");
        PermissionEntity moderateContentPerm = getOrCreatePermission("MODERATE_CONTENT", "Modérer les messages et commentaires");
        PermissionEntity managePaymentsPerm = getOrCreatePermission("MANAGE_PAYMENTS", "Gérer les transactions et confirmer les retraits");
        PermissionEntity userReadPerm = getOrCreatePermission("USER_READ", "Lire les détails des utilisateurs");
        PermissionEntity userWritePerm = getOrCreatePermission("USER_WRITE", "Créer et modifier des utilisateurs");
        PermissionEntity adminDashboardPerm = getOrCreatePermission("ADMIN_DASHBOARD", "Accès au tableau de bord administrateur");
        PermissionEntity poolModeratePerm = getOrCreatePermission("POOL_MODERATE", "Modérer le contenu des cagnottes");
        PermissionEntity paymentReadPerm = getOrCreatePermission("PAYMENT_READ", "Voir les transactions financières");
        PermissionEntity paymentConfirmPerm = getOrCreatePermission("PAYMENT_CONFIRM", "Confirmer les retraits d'argent");

        // 2. Initialiser les rôles si ils n'existent pas
        Set<PermissionEntity> superAdminPerms = Set.of(
            allPerm, manageUsersPerm, manageRolesPerm, validatePoolsPerm, moderateContentPerm, managePaymentsPerm,
            userReadPerm, userWritePerm, adminDashboardPerm, poolModeratePerm, paymentReadPerm, paymentConfirmPerm
        );
        RoleEntity superAdminRole = getOrCreateRole("SUPER_ADMIN", "Super administrateur du système", superAdminPerms);
        
        Set<PermissionEntity> adminPerms = Set.of(
            manageUsersPerm, manageRolesPerm, validatePoolsPerm, moderateContentPerm, managePaymentsPerm,
            userReadPerm, userWritePerm, adminDashboardPerm, poolModeratePerm, paymentReadPerm, paymentConfirmPerm
        );
        RoleEntity adminRole = getOrCreateRole("ADMIN", "Administrateur général", adminPerms);
        
        RoleEntity poolAdminRole = getOrCreateRole("ADMIN_POOL", "Administrateur des cagnottes", Set.of(validatePoolsPerm, poolModeratePerm));
        RoleEntity paymentAdminRole = getOrCreateRole("ADMIN_PAYMENT", "Administrateur des paiements", Set.of(managePaymentsPerm, paymentReadPerm, paymentConfirmPerm));
        RoleEntity moderatorRole = getOrCreateRole("MODERATEUR", "Modérateur de contenu", Set.of(moderateContentPerm, poolModeratePerm));
        RoleEntity userRole = getOrCreateRole("USER", "Utilisateur standard", Set.of());

        // 3. Initialiser les comptes de test s'ils n'existent pas
        createTestUserIfAbsent("superadmin@potify.com", "superadmin", "Super Admin", superAdminRole);
        createTestUserIfAbsent("admin@potify.com", "admin123", "Admin General", adminRole);
        createTestUserIfAbsent("moderator@potify.com", "admin123", "Modérateur Potify", moderatorRole);
        createTestUserIfAbsent("pooladmin@potify.com", "admin123", "Admin Cagnottes", poolAdminRole);
        createTestUserIfAbsent("paymentadmin@potify.com", "admin123", "Admin Paiements", paymentAdminRole);
        createTestUserIfAbsent("user@potify.com", "user123", "Utilisateur Simple", userRole);

        System.out.println(">>> [Initialisation] Comptes et rôles configurés avec succès.");
    }

    private PermissionEntity getOrCreatePermission(String code, String description) {
        return permissionRepository.findByCode(code)
                .orElseGet(() -> permissionRepository.save(
                        PermissionEntity.builder()
                                .code(code)
                                .description(description)
                                .build()
                ));
    }

    private RoleEntity getOrCreateRole(String name, String description, Set<PermissionEntity> permissions) {
        RoleEntity role = roleRepository.findByName(name).orElse(null);
        if (role == null) {
            role = RoleEntity.builder()
                    .name(name)
                    .description(description)
                    .permissions(new HashSet<>(permissions))
                    .build();
        } else {
            role.setPermissions(new HashSet<>(permissions));
        }
        return roleRepository.save(role);
    }

    private void createTestUserIfAbsent(String email, String password, String fullName, RoleEntity role) {
        if (userRepository.findByEmail(email).isEmpty()) {
            UserEntity user = UserEntity.builder()
                    .fullName(fullName)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .status(UserStatus.ACTIVE)
                    .enabled(true)
                    .accountNonLocked(true)
                    .roles(new HashSet<>(Set.of(role)))
                    .build();
            userRepository.save(user);
            System.out.println(">>> [Initialisation] Utilisateur de test créé : " + email + " / " + password + " (" + role.getName() + ")");
        }
    }
}
