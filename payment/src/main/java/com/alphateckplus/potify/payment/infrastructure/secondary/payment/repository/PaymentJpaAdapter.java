package com.alphateckplus.potify.payment.infrastructure.secondary.payment.repository;

import com.alphateckplus.potify.data_jpa.entity.payment.ContributionEntity;
import com.alphateckplus.potify.data_jpa.entity.payment.TransactionEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.CagnotteWalletEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.payment.ContributionEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.payment.TransactionEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.CagnotteWalletEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.payment.application_service.secondary.payment.PaymentRepositoryPort;
import com.alphateckplus.potify.payment.domain.model.Contribution;
import com.alphateckplus.potify.payment.domain.model.ContributionStatus;
import com.alphateckplus.potify.payment.domain.model.Transaction;
import com.alphateckplus.potify.payment.domain.model.TransactionStatus;
import com.alphateckplus.potify.payment.domain.model.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Transactional
@RequiredArgsConstructor
public class PaymentJpaAdapter implements PaymentRepositoryPort {

    private final ContributionEntityRepository contributionRepository;
    private final PoolEntityRepository poolRepository;
    private final UserEntityRepository userRepository;
    private final CagnotteWalletEntityRepository walletRepository;
    private final TransactionEntityRepository transactionRepository;

    @Override
    public Contribution saveContribution(Contribution domain) {
        ContributionEntity entity;
        if (domain.getId() != null) {
            entity = contributionRepository.findById(domain.getId()).orElse(new ContributionEntity());
        } else {
            entity = new ContributionEntity();
        }

        entity.setId(domain.getId());
        entity.setAmount(domain.getAmount());
        entity.setContributorEmail(domain.getContributorEmail());
        entity.setContributorName(domain.getContributorName());
        entity.setMessage(domain.getMessage());
        entity.setAnonymous(domain.isAnonymous());
        entity.setPaymentMethod(domain.getPaymentMethod());
        entity.setStatus(mapStatusToEntity(domain.getStatus()));

        if (domain.getPoolId() != null) {
            PoolEntity pool = poolRepository.findById(domain.getPoolId())
                    .orElseThrow(() -> new IllegalArgumentException("Cagnotte introuvable: " + domain.getPoolId()));
            entity.setPool(pool);
            
            CagnotteWalletEntity wallet = walletRepository.findByPoolId(domain.getPoolId())
                    .orElse(null);
            entity.setWallet(wallet);
        }

        if (domain.getUserId() != null) {
            UserEntity user = userRepository.findById(domain.getUserId()).orElse(null);
            entity.setUser(user);
        }

        ContributionEntity saved = contributionRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<Contribution> findContributionById(String id) {
        return contributionRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public Optional<Contribution> findContributionByPayPalOrderId(String orderId) {
        return findContributionByPaymentMethod("PAYPAL:" + orderId);
    }

    @Override
    public Optional<Contribution> findContributionByPaymentMethod(String paymentMethod) {
        return contributionRepository.findAll().stream()
                .filter(c -> paymentMethod.equals(c.getPaymentMethod()))
                .findFirst()
                .map(this::mapToDomain);
    }

    @Override
    public List<Contribution> findContributionsByPoolId(String poolId) {
        return contributionRepository.findByPoolId(poolId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Contribution> findContributionsByUserId(String userId) {
        return contributionRepository.findByUserId(userId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Transaction saveTransaction(Transaction domain) {
        TransactionEntity entity;
        if (domain.getId() != null) {
            entity = transactionRepository.findById(domain.getId()).orElse(new TransactionEntity());
        } else {
            entity = new TransactionEntity();
        }

        entity.setId(domain.getId());
        entity.setAmount(domain.getAmount());
        entity.setFees(domain.getFees());
        entity.setType(mapTypeToEntity(domain.getType()));
        entity.setStatus(mapStatusToEntity(domain.getStatus()));

        if (domain.getWalletId() != null) {
            CagnotteWalletEntity wallet = walletRepository.findById(domain.getWalletId())
                    .orElseGet(() -> walletRepository.findByPoolId(domain.getWalletId()).orElse(null));
            if (wallet == null) {
                throw new IllegalArgumentException("Portefeuille introuvable: " + domain.getWalletId());
            }
            entity.setWallet(wallet);
        }

        if (domain.getContributionId() != null) {
            ContributionEntity contribution = contributionRepository.findById(domain.getContributionId()).orElse(null);
            entity.setContribution(contribution);
        }

        TransactionEntity saved = transactionRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public List<Transaction> findAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void creditPoolAndWallet(String poolId, BigDecimal amount) {
        PoolEntity pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte introuvable"));
        pool.setCurrentAmount(pool.getCurrentAmount().add(amount));
        poolRepository.save(pool);

        CagnotteWalletEntity wallet = walletRepository.findByPoolId(poolId)
                .orElseThrow(() -> new IllegalArgumentException("Portefeuille introuvable"));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        walletRepository.save(wallet);

        // Propagation récursive aux parents
        PoolEntity parent = pool.getParent();
        while (parent != null) {
            parent.setCurrentAmount(parent.getCurrentAmount().add(amount));
            poolRepository.save(parent);
            
            CagnotteWalletEntity parentWallet = parent.getWallet();
            if (parentWallet != null) {
                parentWallet.setAvailableBalance(parentWallet.getAvailableBalance().add(amount));
                walletRepository.save(parentWallet);
            }
            parent = parent.getParent();
        }
    }

    // Mappers
    private Contribution mapToDomain(ContributionEntity entity) {
        if (entity == null) return null;
        return Contribution.builder()
                .id(entity.getId())
                .poolId(entity.getPool() != null ? entity.getPool().getId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .amount(entity.getAmount())
                .contributorEmail(entity.getContributorEmail())
                .contributorName(entity.getContributorName())
                .message(entity.getMessage())
                .anonymous(entity.isAnonymous())
                .paymentMethod(entity.getPaymentMethod())
                .status(mapStatusToDomain(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private Transaction mapToDomain(TransactionEntity entity) {
        if (entity == null) return null;
        return Transaction.builder()
                .id(entity.getId())
                .walletId(entity.getWallet() != null ? entity.getWallet().getId() : null)
                .contributionId(entity.getContribution() != null ? entity.getContribution().getId() : null)
                .type(mapTypeToDomain(entity.getType()))
                .amount(entity.getAmount())
                .fees(entity.getFees())
                .status(mapStatusToDomain(entity.getStatus()))
                .build();
    }

    private ContributionStatus mapStatusToDomain(com.alphateckplus.potify.data_jpa.entity.payment.ContributionStatus status) {
        if (status == null) return null;
        return ContributionStatus.valueOf(status.name());
    }

    private com.alphateckplus.potify.data_jpa.entity.payment.ContributionStatus mapStatusToEntity(ContributionStatus status) {
        if (status == null) return null;
        return com.alphateckplus.potify.data_jpa.entity.payment.ContributionStatus.valueOf(status.name());
    }

    private TransactionStatus mapStatusToDomain(com.alphateckplus.potify.data_jpa.entity.payment.TransactionStatus status) {
        if (status == null) return null;
        return TransactionStatus.valueOf(status.name());
    }

    private com.alphateckplus.potify.data_jpa.entity.payment.TransactionStatus mapStatusToEntity(TransactionStatus status) {
        if (status == null) return null;
        return com.alphateckplus.potify.data_jpa.entity.payment.TransactionStatus.valueOf(status.name());
    }

    private TransactionType mapTypeToDomain(com.alphateckplus.potify.data_jpa.entity.payment.TransactionType type) {
        if (type == null) return null;
        return TransactionType.valueOf(type.name());
    }

    private com.alphateckplus.potify.data_jpa.entity.payment.TransactionType mapTypeToEntity(TransactionType type) {
        if (type == null) return null;
        return com.alphateckplus.potify.data_jpa.entity.payment.TransactionType.valueOf(type.name());
    }

    @Override
    public String getPoolOwnerId(String poolId) {
        return poolRepository.findById(poolId)
                .map(pool -> pool.getOwner().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte introuvable: " + poolId));
    }

    @Override
    public String getPoolTitle(String poolId) {
        return poolRepository.findById(poolId)
                .map(com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity::getTitle)
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte introuvable: " + poolId));
    }
}
