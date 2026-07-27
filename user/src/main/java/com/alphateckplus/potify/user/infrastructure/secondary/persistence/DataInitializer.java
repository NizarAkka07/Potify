package com.alphateckplus.potify.user.infrastructure.secondary.persistence;

// Importation des entités de persistance du module data-jpa
import com.alphateckplus.potify.data_jpa.entity.user.PermissionEntity;
import com.alphateckplus.potify.data_jpa.entity.user.RoleEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserStatus;
import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.CagnotteStatus;
import com.alphateckplus.potify.data_jpa.entity.pool.CagnotteWalletEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.MessageEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.MessageReportEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.PoolReportEntity;
import com.alphateckplus.potify.data_jpa.entity.payment.TransactionEntity;
import com.alphateckplus.potify.data_jpa.entity.payment.TransactionType;
import com.alphateckplus.potify.data_jpa.entity.payment.TransactionStatus;

// Importation des repositories pour l'accès direct à la base de données lors de l'initialisation
import com.alphateckplus.potify.data_jpa.repository.user.PermissionEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.RoleEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.CagnotteWalletEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.MessageEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.MessageReportEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolReportEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.payment.TransactionEntityRepository;

// Importation des utilitaires Java et Spring
import java.math.BigDecimal;
import java.time.Instant;
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

    private final PoolEntityRepository poolRepository;
    private final CagnotteWalletEntityRepository walletRepository;
    private final MessageEntityRepository messageRepository;
    private final MessageReportEntityRepository messageReportRepository;
    private final PoolReportEntityRepository poolReportRepository;
    private final TransactionEntityRepository transactionRepository;

    /**
     * Méthode exécutée au démarrage.
     * @param args Arguments de la ligne de commande (non utilisés ici).
     */
    @Override
    @Transactional // Garantit que toute l'initialisation se fait dans une seule transaction atomique
    public void run(String... args) {
        // Nettoyage des contraintes obsolètes (Hibernate génère des CHECK constraints rigides)
        try {
            System.out.println(">>> [Nettoyage] Suppression des contraintes check obsolètes...");
            jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_status_check");
            jdbcTemplate.execute("ALTER TABLE transactions DROP CONSTRAINT IF EXISTS transactions_status_check");
            jdbcTemplate.execute("ALTER TABLE transactions DROP CONSTRAINT IF EXISTS transactions_type_check");
            jdbcTemplate.execute("ALTER TABLE pools DROP CONSTRAINT IF EXISTS pools_status_check");
            jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS admin_verification BOOLEAN NOT NULL DEFAULT FALSE");
            System.out.println(">>> [Nettoyage] Contraintes supprimées et colonne admin_verification configurée avec succès.");
        } catch (Exception e) {
            System.out.println(">>> [Nettoyage] Info: Pas de contrainte à supprimer ou erreur : " + e.getMessage());
        }

        // Vérification du nombre d'utilisateurs actuels
        long userCount = userRepository.count();
        System.out.println(">>> [Initialisation] Nombre d'utilisateurs en base : " + userCount);
        
        // 1. Initialiser les permissions si elles n'existent pas
        PermissionEntity allPerm = getOrCreatePermission("ALL", "Accès complet à toutes les ressources");

        // Permissions pour ADMIN_PAYMENT
        PermissionEntity paymentReadPerm = getOrCreatePermission("PAYMENT_READ", "Voir les transactions financières");
        PermissionEntity paymentConfirmPerm = getOrCreatePermission("PAYMENT_CONFIRM", "Confirmer les retraits d'argent");
        PermissionEntity paymentRefundPerm = getOrCreatePermission("PAYMENT_REFUND", "Rembourser des contributions");
        PermissionEntity exportFinancialPerm = getOrCreatePermission("EXPORT_FINANCIAL", "Exporter les données comptables et financières");

        // Permissions pour ADMIN_POOL
        PermissionEntity poolReadPerm = getOrCreatePermission("POOL_READ", "Voir les détails de toutes les cagnottes");
        PermissionEntity poolValidatePerm = getOrCreatePermission("POOL_VALIDATE", "Valider et publier les cagnottes");
        PermissionEntity poolSuspendPerm = getOrCreatePermission("POOL_SUSPEND", "Suspendre des cagnottes");
        PermissionEntity poolArchivePerm = getOrCreatePermission("POOL_ARCHIVE", "Archiver des cagnottes");

        // Permissions pour MODERATEUR
        PermissionEntity reportReadPerm = getOrCreatePermission("REPORT_READ", "Voir les signalements de messages");
        PermissionEntity reportResolvePerm = getOrCreatePermission("REPORT_RESOLVE", "Résoudre les signalements");
        PermissionEntity messageDeletePerm = getOrCreatePermission("MESSAGE_DELETE", "Supprimer les messages modérés");

        // Permissions pour SUPPORT
        PermissionEntity supportQueueViewPerm = getOrCreatePermission("SUPPORT_QUEUE_VIEW", "Accès au tableau de bord et à la file d'attente support");
        PermissionEntity supportChatAssignPerm = getOrCreatePermission("SUPPORT_CHAT_ASSIGN", "Prendre en charge un ticket de support");
        PermissionEntity supportChatReplyPerm = getOrCreatePermission("SUPPORT_CHAT_REPLY", "Répondre aux messages de support");
        PermissionEntity supportChatTransferPerm = getOrCreatePermission("SUPPORT_CHAT_TRANSFER", "Transférer un ticket de support à un autre agent");
        PermissionEntity supportChatClosePerm = getOrCreatePermission("SUPPORT_CHAT_CLOSE", "Résoudre et fermer un ticket de support");

        // Permissions pour USER
        PermissionEntity poolCreatePerm = getOrCreatePermission("POOL_CREATE", "Créer une cagnotte");
        PermissionEntity poolUpdateOwnPerm = getOrCreatePermission("POOL_UPDATE_OWN", "Modifier sa propre cagnotte");
        PermissionEntity poolCloseOwnPerm = getOrCreatePermission("POOL_CLOSE_OWN", "Clôturer sa propre cagnotte");
        PermissionEntity contributionCreatePerm = getOrCreatePermission("CONTRIBUTION_CREATE", "Contribuer à une cagnotte");
        PermissionEntity messageCreateOwnPerm = getOrCreatePermission("MESSAGE_CREATE_OWN", "Ajouter un message sur une cagnotte");

        Set<PermissionEntity> allSuperAdminPerms = Set.of(allPerm, paymentReadPerm, paymentConfirmPerm, paymentRefundPerm, exportFinancialPerm, poolReadPerm, poolValidatePerm, poolSuspendPerm, poolArchivePerm, reportReadPerm, reportResolvePerm, messageDeletePerm, supportQueueViewPerm, supportChatAssignPerm, supportChatReplyPerm, supportChatTransferPerm, supportChatClosePerm);
        RoleEntity superAdminRole = getOrCreateRole("SUPER_ADMIN", "Super administrateur du système", allSuperAdminPerms);
        
        Set<PermissionEntity> paymentAdminPerms = Set.of(paymentReadPerm, paymentConfirmPerm, paymentRefundPerm, exportFinancialPerm);
        RoleEntity paymentAdminRole = getOrCreateRole("ADMIN_PAYMENT", "Administrateur des paiements et retraits", paymentAdminPerms);
        
        Set<PermissionEntity> poolAdminPerms = Set.of(poolReadPerm, poolValidatePerm, poolSuspendPerm, poolArchivePerm);
        RoleEntity poolAdminRole = getOrCreateRole("ADMIN_POOL", "Administrateur des cagnottes", poolAdminPerms);
        
        Set<PermissionEntity> moderatorPerms = Set.of(reportReadPerm, reportResolvePerm, messageDeletePerm);
        RoleEntity moderatorRole = getOrCreateRole("MODERATEUR", "Modérateur de contenu pour messages", moderatorPerms);
        
        Set<PermissionEntity> supportAgentPerms = Set.of(supportQueueViewPerm, supportChatAssignPerm, supportChatReplyPerm, supportChatTransferPerm, supportChatClosePerm);
        RoleEntity supportAgentRole = getOrCreateRole("SUPPORT_AGENT", "Agent de support client", supportAgentPerms);

        Set<PermissionEntity> userPerms = Set.of(poolCreatePerm, poolUpdateOwnPerm, poolCloseOwnPerm, contributionCreatePerm, messageCreateOwnPerm);
        RoleEntity userRole = getOrCreateRole("USER", "Utilisateur standard", userPerms);

        // 3. Initialiser les comptes de test s'ils n'existent pas
        createTestUserIfAbsent("superadmin@potify.com", "superadmin", "Super Admin", superAdminRole);
        createTestUserIfAbsent("supportagent@potify.com", "admin123", "Agent Support", supportAgentRole);
        createTestUserIfAbsent("moderator@potify.com", "admin123", "Modérateur Potify", moderatorRole);
        createTestUserIfAbsent("pooladmin@potify.com", "admin123", "Admin Cagnottes", poolAdminRole);
        createTestUserIfAbsent("paymentadmin@potify.com", "admin123", "Admin Paiements", paymentAdminRole);
        createTestUserIfAbsent("user@potify.com", "user123", "Utilisateur Simple", userRole);

        System.out.println(">>> [Initialisation] Comptes et rôles configurés avec succès.");

        // 4. Initialiser les données simulées (Cagnottes, Messages, Transactions, Signalements)
        initializeMockData();
    }

    private void initializeMockData() {
        if (poolRepository.count() == 0) {
            System.out.println(">>> [Initialisation] Création des données de test (Cagnottes, Commentaires, Retraits, Signalements)...");
            
            UserEntity userSimple = userRepository.findByEmail("user@potify.com").orElse(null);
            UserEntity poolAdmin = userRepository.findByEmail("pooladmin@potify.com").orElse(null);
            
            if (userSimple == null) {
                System.out.println(">>> [Initialisation] Erreur : Utilisateur de test user@potify.com manquant.");
                return;
            }

            // Pool 1: Aide pour Sofia (Santé) - Publiée
            PoolEntity sofiaPool = new PoolEntity();
            sofiaPool.setOwner(userSimple);
            sofiaPool.setTitle("Aide pour l'opération cardiaque de Sofia");
            sofiaPool.setDescription("Sofia, 6 ans, souffre d'une malformation cardiaque grave. Cette cagnotte vise à financer son opération chirurgicale urgente prévue à la fin du mois.");
            sofiaPool.setCategory("Santé");
            sofiaPool.setGoalAmount(new BigDecimal("15000.00"));
            sofiaPool.setCurrentAmount(new BigDecimal("7500.00"));
            sofiaPool.setStatus(CagnotteStatus.PUBLIEE);
            sofiaPool.setType("PUBLIC");
            sofiaPool.setImageUrl("https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=500&auto=format&fit=crop&q=60");
            sofiaPool.setFees(new BigDecimal("2.00"));
            sofiaPool.setCreatedAt(Instant.now());
            sofiaPool.setUpdatedAt(Instant.now());
            sofiaPool = poolRepository.save(sofiaPool);
            
            CagnotteWalletEntity walletSofia = new CagnotteWalletEntity();
            walletSofia.setPool(sofiaPool);
            walletSofia.setAvailableBalance(new BigDecimal("7500.00"));
            walletSofia.setPendingBalance(BigDecimal.ZERO);
            walletSofia.setCreatedAt(Instant.now());
            walletSofia.setUpdatedAt(Instant.now());
            walletSofia = walletRepository.save(walletSofia);
            sofiaPool.setWallet(walletSofia);
            poolRepository.save(sofiaPool);

            // Pool 2: Bibliothèque du village (Éducation) - Publiée
            PoolEntity libraryPool = new PoolEntity();
            libraryPool.setOwner(userSimple);
            libraryPool.setTitle("Reconstruction de la bibliothèque du village");
            libraryPool.setDescription("Rénovation des locaux et achat de nouveaux livres pour la bibliothèque détruite par l'incendie de l'hiver dernier. Rendons le savoir accessible à nos enfants.");
            libraryPool.setCategory("Éducation");
            libraryPool.setGoalAmount(new BigDecimal("8000.00"));
            libraryPool.setCurrentAmount(new BigDecimal("5200.00"));
            libraryPool.setStatus(CagnotteStatus.PUBLIEE);
            libraryPool.setType("PUBLIC");
            libraryPool.setImageUrl("https://images.unsplash.com/photo-1521587760476-6c12a4b040da?w=500&auto=format&fit=crop&q=60");
            libraryPool.setFees(new BigDecimal("2.00"));
            libraryPool.setCreatedAt(Instant.now());
            libraryPool.setUpdatedAt(Instant.now());
            libraryPool = poolRepository.save(libraryPool);

            CagnotteWalletEntity walletLibrary = new CagnotteWalletEntity();
            walletLibrary.setPool(libraryPool);
            walletLibrary.setAvailableBalance(new BigDecimal("5200.00"));
            walletLibrary.setPendingBalance(BigDecimal.ZERO);
            walletLibrary.setCreatedAt(Instant.now());
            walletLibrary.setUpdatedAt(Instant.now());
            walletLibrary = walletRepository.save(walletLibrary);
            libraryPool.setWallet(walletLibrary);
            poolRepository.save(libraryPool);

            // Pool 3: Refuge de l'Espoir (Animaux) - Publiée
            PoolEntity animalPool = new PoolEntity();
            animalPool.setOwner(userSimple);
            animalPool.setTitle("Urgence Animaux : Refuge de l'Espoir");
            animalPool.setDescription("Notre refuge est au maximum de sa capacité. Nous avons besoin d'aide pour financer la nourriture, les vaccins et les soins vétérinaires urgents de nos petits pensionnaires.");
            animalPool.setCategory("Animaux");
            animalPool.setGoalAmount(new BigDecimal("3000.00"));
            animalPool.setCurrentAmount(new BigDecimal("1200.00"));
            animalPool.setStatus(CagnotteStatus.PUBLIEE);
            animalPool.setType("PUBLIC");
            animalPool.setImageUrl("https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=500&auto=format&fit=crop&q=60");
            animalPool.setFees(new BigDecimal("2.00"));
            animalPool.setCreatedAt(Instant.now());
            animalPool.setUpdatedAt(Instant.now());
            animalPool = poolRepository.save(animalPool);

            CagnotteWalletEntity walletAnimal = new CagnotteWalletEntity();
            walletAnimal.setPool(animalPool);
            walletAnimal.setAvailableBalance(new BigDecimal("1200.00"));
            walletAnimal.setPendingBalance(BigDecimal.ZERO);
            walletAnimal.setCreatedAt(Instant.now());
            walletAnimal.setUpdatedAt(Instant.now());
            walletAnimal = walletRepository.save(walletAnimal);
            animalPool.setWallet(walletAnimal);
            poolRepository.save(animalPool);

            // Pool 4: Projet éolienne (Technologie) - En Revue
            PoolEntity windPool = new PoolEntity();
            windPool.setOwner(userSimple);
            windPool.setTitle("Projet Éolienne Domestique 3D");
            windPool.setDescription("Conception et fabrication en open-source d'un prototype d'éolienne domestique imprimable en 3D pour encourager l'énergie propre et autonome.");
            windPool.setCategory("Technologie");
            windPool.setGoalAmount(new BigDecimal("5000.00"));
            windPool.setCurrentAmount(new BigDecimal("0.00"));
            windPool.setStatus(CagnotteStatus.EN_REVUE);
            windPool.setType("PUBLIC");
            windPool.setImageUrl("https://images.unsplash.com/photo-1473341304170-971dccb5ac1e?w=500&auto=format&fit=crop&q=60");
            windPool.setFees(new BigDecimal("2.00"));
            windPool.setCreatedAt(Instant.now());
            windPool.setUpdatedAt(Instant.now());
            windPool = poolRepository.save(windPool);

            CagnotteWalletEntity walletWind = new CagnotteWalletEntity();
            walletWind.setPool(windPool);
            walletWind.setAvailableBalance(BigDecimal.ZERO);
            walletWind.setPendingBalance(BigDecimal.ZERO);
            walletWind.setCreatedAt(Instant.now());
            walletWind.setUpdatedAt(Instant.now());
            walletWind = walletRepository.save(walletWind);
            windPool.setWallet(walletWind);
            poolRepository.save(windPool);

            // Pool 5: Cagnotte Signalée (Autre) - Publiée
            PoolEntity reportedPool = new PoolEntity();
            reportedPool.setOwner(userSimple);
            reportedPool.setTitle("Collecte Suspecte Non Conforme");
            reportedPool.setDescription("Cette cagnotte est suspectée de violer les conditions d'utilisation de Potify pour suspicion de fraude.");
            reportedPool.setCategory("Autre");
            reportedPool.setGoalAmount(new BigDecimal("10000.00"));
            reportedPool.setCurrentAmount(new BigDecimal("1000.00"));
            reportedPool.setStatus(CagnotteStatus.PUBLIEE);
            reportedPool.setType("PUBLIC");
            reportedPool.setImageUrl("https://images.unsplash.com/photo-1535262412227-85541e910204?w=500&auto=format&fit=crop&q=60");
            reportedPool.setFees(new BigDecimal("2.00"));
            reportedPool.setCreatedAt(Instant.now());
            reportedPool.setUpdatedAt(Instant.now());
            reportedPool = poolRepository.save(reportedPool);

            CagnotteWalletEntity walletReported = new CagnotteWalletEntity();
            walletReported.setPool(reportedPool);
            walletReported.setAvailableBalance(new BigDecimal("1000.00"));
            walletReported.setPendingBalance(BigDecimal.ZERO);
            walletReported.setCreatedAt(Instant.now());
            walletReported.setUpdatedAt(Instant.now());
            walletReported = walletRepository.save(walletReported);
            reportedPool.setWallet(walletReported);
            poolRepository.save(reportedPool);

            // Create comments/messages
            MessageEntity comment1 = new MessageEntity();
            comment1.setPool(sofiaPool);
            comment1.setUser(userSimple);
            comment1.setContent("Tous mes voeux de réussite pour l'opération de la petite Sofia !");
            comment1.setPublic(true);
            comment1.setCreatedAt(Instant.now());
            comment1.setUpdatedAt(Instant.now());
            messageRepository.save(comment1);

            MessageEntity comment2 = new MessageEntity();
            comment2.setPool(sofiaPool);
            comment2.setUser(poolAdmin);
            comment2.setContent("Notre équipe administrative suit ce projet avec attention, merci pour votre générosité.");
            comment2.setPublic(true);
            comment2.setCreatedAt(Instant.now());
            comment2.setUpdatedAt(Instant.now());
            messageRepository.save(comment2);

            MessageEntity commentSpam = new MessageEntity();
            commentSpam.setPool(sofiaPool);
            commentSpam.setUser(userSimple);
            commentSpam.setContent("Visitez notre site de crypto-monnaies gratuites !!! ARNAQUE TOTALE");
            commentSpam.setPublic(true);
            commentSpam.setCreatedAt(Instant.now());
            commentSpam.setUpdatedAt(Instant.now());
            commentSpam = messageRepository.save(commentSpam);

            // Create message report
            MessageReportEntity msgReport = new MessageReportEntity();
            msgReport.setMessage(commentSpam);
            msgReport.setUser(poolAdmin);
            msgReport.setReason("Contenu publicitaire indésirable (Spam)");
            msgReport.setCreatedAt(Instant.now());
            msgReport.setUpdatedAt(Instant.now());
            messageReportRepository.save(msgReport);

            // Create pool report
            PoolReportEntity pReport = new PoolReportEntity();
            pReport.setPool(reportedPool);
            pReport.setUser(userSimple);
            pReport.setReason("Suspicion de fraude et détournement de fonds.");
            pReport.setCreatedAt(Instant.now());
            pReport.setUpdatedAt(Instant.now());
            poolReportRepository.save(pReport);

            // Create pending withdrawal transactions
            TransactionEntity withdrawal1 = new TransactionEntity();
            withdrawal1.setWallet(walletSofia);
            withdrawal1.setType(TransactionType.PAYOUT);
            withdrawal1.setAmount(new BigDecimal("1500.00"));
            withdrawal1.setFees(new BigDecimal("30.00"));
            withdrawal1.setStatus(TransactionStatus.PENDING);
            withdrawal1.setIban("FR7630006000012345678901234");
            withdrawal1.setAccountHolderName("Sofia Martin");
            withdrawal1.setBankName("Société Générale");
            withdrawal1.setCreatedAt(Instant.now());
            withdrawal1.setUpdatedAt(Instant.now());
            transactionRepository.save(withdrawal1);

            TransactionEntity withdrawal2 = new TransactionEntity();
            withdrawal2.setWallet(walletLibrary);
            withdrawal2.setType(TransactionType.PAYOUT);
            withdrawal2.setAmount(new BigDecimal("850.00"));
            withdrawal2.setFees(new BigDecimal("17.00"));
            withdrawal2.setStatus(TransactionStatus.PENDING);
            withdrawal2.setIban("FR7610002000098765432109876");
            withdrawal2.setAccountHolderName("Jean Dupont");
            withdrawal2.setBankName("BNP Paribas");
            withdrawal2.setCreatedAt(Instant.now());
            withdrawal2.setUpdatedAt(Instant.now());
            transactionRepository.save(withdrawal2);

            System.out.println(">>> [Initialisation] Données de test créées avec succès !");
        }
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
                    .adminVerification(true)
                    .roles(new HashSet<>(Set.of(role)))
                    .build();
            userRepository.save(user);
            System.out.println(">>> [Initialisation] Utilisateur de test créé : " + email + " / " + password + " (" + role.getName() + ")");
        }
    }
}
