package com.alphateckplus.potify.pool.application_service.primary.pool.create_pool;

import com.alphateckplus.potify.pool.application_service.primary.command.CreatePoolCommand;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.WalletRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Pool;
import com.alphateckplus.potify.pool.domain.model.PoolStatus;
import com.alphateckplus.potify.pool.domain.model.Wallet;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Implementation par defaut du service de creation de cagnotte.
 */
public class DefaultCreatePoolService implements CreatePoolService {

    private final PoolRepositoryPort poolRepositoryPort;
    private final WalletRepositoryPort walletRepositoryPort;

    public DefaultCreatePoolService(PoolRepositoryPort poolRepositoryPort, WalletRepositoryPort walletRepositoryPort) {
        this.poolRepositoryPort = poolRepositoryPort;
        this.walletRepositoryPort = walletRepositoryPort;
    }

    @Override
    public Pool execute(CreatePoolCommand command) {
        BigDecimal mainGoal = BigDecimal.ZERO;
        if (command.subPools() != null && !command.subPools().isEmpty()) {
            for (com.alphateckplus.potify.pool.application_service.primary.command.SubPoolCommand subPoolCmd : command.subPools()) {
                BigDecimal subPoolGoal = subPoolCmd.phases().stream()
                        .map(com.alphateckplus.potify.pool.application_service.primary.command.PhaseCommand::goalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                mainGoal = mainGoal.add(subPoolGoal);
            }
        } else if (command.phases() != null && !command.phases().isEmpty()) {
            mainGoal = command.phases().stream()
                    .map(com.alphateckplus.potify.pool.application_service.primary.command.PhaseCommand::goalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            mainGoal = command.goalAmount() != null ? command.goalAmount() : BigDecimal.ZERO;
        }

        Pool pool = Pool.builder()
                .ownerId(command.ownerId())
                .title(command.title())
                .description(command.description())
                .category(command.category())
                .goalAmount(mainGoal)
                .currentAmount(BigDecimal.ZERO)
                .status(PoolStatus.PUBLIEE)
                .type(command.type())
                .invitedUserIds(command.invitedUserIds())
                .imageContent(command.imageContent())
                .imageContentType(command.imageContentType())
                .videoUrl(command.videoUrl())
                .parentId(command.parentId())
                .hasDeadline(command.hasDeadline() != null ? command.hasDeadline() : false)
                .deadlineDate(command.deadlineDate())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        if (command.phases() != null && !command.phases().isEmpty()) {
            java.util.List<com.alphateckplus.potify.pool.domain.model.Phase> phases = new java.util.ArrayList<>();
            for (com.alphateckplus.potify.pool.application_service.primary.command.PhaseCommand pc : command.phases()) {
                phases.add(com.alphateckplus.potify.pool.domain.model.Phase.builder()
                        .title(pc.title())
                        .goalAmount(pc.goalAmount())
                        .status(com.alphateckplus.potify.pool.domain.model.PhaseStatus.ACTIVE)
                        .build());
            }
            pool.setPhases(phases);
        }

        Pool savedPool = poolRepositoryPort.save(pool);
        walletRepositoryPort.save(Wallet.createEmpty(savedPool.getId()));

        if (command.subPools() != null && !command.subPools().isEmpty()) {
            for (com.alphateckplus.potify.pool.application_service.primary.command.SubPoolCommand subPoolCmd : command.subPools()) {
                java.util.List<com.alphateckplus.potify.pool.domain.model.Phase> subPhases = new java.util.ArrayList<>();
                for (com.alphateckplus.potify.pool.application_service.primary.command.PhaseCommand pc : subPoolCmd.phases()) {
                    subPhases.add(com.alphateckplus.potify.pool.domain.model.Phase.builder()
                            .title(pc.title())
                            .goalAmount(pc.goalAmount())
                            .status(com.alphateckplus.potify.pool.domain.model.PhaseStatus.ACTIVE)
                            .build());
                }
                BigDecimal subGoal = subPhases.stream()
                        .map(com.alphateckplus.potify.pool.domain.model.Phase::getGoalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                Pool subPool = Pool.builder()
                        .ownerId(command.ownerId())
                        .title(subPoolCmd.title())
                        .description(subPoolCmd.description())
                        .category(command.category())
                        .goalAmount(subGoal)
                        .currentAmount(BigDecimal.ZERO)
                        .status(PoolStatus.PUBLIEE)
                        .type(command.type())
                        .parentId(savedPool.getId())
                        .hasDeadline(subPoolCmd.hasDeadline() != null ? subPoolCmd.hasDeadline() : false)
                        .deadlineDate(subPoolCmd.deadlineDate())
                        .phases(subPhases)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                Pool savedSubPool = poolRepositoryPort.save(subPool);
                walletRepositoryPort.save(Wallet.createEmpty(savedSubPool.getId()));
            }
        }
        
        return savedPool;
    }
}
