package com.alphateckplus.potify.pool.application_service.tontine;

import com.alphateckplus.potify.data_jpa.entity.payment.ContributionEntity;
import com.alphateckplus.potify.data_jpa.entity.payment.ContributionStatus;
import com.alphateckplus.potify.data_jpa.entity.payment.PayoutEntity;
import com.alphateckplus.potify.data_jpa.entity.payment.PayoutStatus;
import com.alphateckplus.potify.data_jpa.entity.pool.CagnotteStatus;
import com.alphateckplus.potify.data_jpa.entity.pool.CagnotteWalletEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.PoolInvitationEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.TontineDetailsEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.TontineFrequency;
import com.alphateckplus.potify.data_jpa.entity.pool.TontineMemberEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.TontineRoundStatus;
import com.alphateckplus.potify.data_jpa.entity.pool.TontineStatus;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.payment.ContributionEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.payment.PayoutEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.CagnotteWalletEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolInvitationEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.TontineDetailsEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.TontineMemberEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.tontine.dto.ConfigureTontineRequest;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.tontine.dto.TontineMemberDto;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.tontine.dto.TontineRoundDto;
import com.alphateckplus.potify.pool.infrastructure.primary.pool.tontine.dto.TontineSummaryResponse;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TontineService {

    private final PoolEntityRepository poolRepository;
    private final TontineDetailsEntityRepository tontineDetailsRepository;
    private final TontineMemberEntityRepository tontineMemberRepository;
    private final UserEntityRepository userRepository;
    private final PoolInvitationEntityRepository invitationRepository;
    private final ContributionEntityRepository contributionRepository;
    private final PayoutEntityRepository payoutRepository;
    private final CagnotteWalletEntityRepository walletRepository;

    @Transactional
    public TontineDetailsEntity configureTontine(String poolId, ConfigureTontineRequest request) {
        PoolEntity pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte introuvable : " + poolId));

        TontineDetailsEntity details = tontineDetailsRepository.findByPoolId(poolId)
                .orElseGet(() -> TontineDetailsEntity.builder()
                        .pool(pool)
                        .status(TontineStatus.DRAFT)
                        .currentRoundNumber(1)
                        .build());

        details.setContributionAmount(request.contributionAmount());
        details.setFrequency(request.frequency());
        details.setLatePenaltyRate(request.latePenaltyRate() != null ? request.latePenaltyRate() : BigDecimal.ZERO);

        TontineDetailsEntity savedDetails = tontineDetailsRepository.save(details);

        // La tontine démarre immédiatement dès la création / configuration !
        startTontineInternal(pool, savedDetails);

        return savedDetails;
    }

    @Transactional
    public TontineSummaryResponse startTontine(String poolId) {
        PoolEntity pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte introuvable : " + poolId));

        TontineDetailsEntity details = tontineDetailsRepository.findByPoolId(poolId)
                .orElseThrow(() -> new IllegalStateException("Veuillez d'abord configurer les paramètres de la tontine."));

        startTontineInternal(pool, details);
        return getTontineSummary(poolId);
    }

    private void startTontineInternal(PoolEntity pool, TontineDetailsEntity details) {
        List<UserEntity> membersList = countValidMembers(pool);
        if (membersList.isEmpty()) {
            membersList.add(pool.getOwner());
        }

        // 1. Créer les TontineMemberEntity avec un ordre fixe (1..N)
        tontineMemberRepository.findByPoolIdOrderByOrderIndexAsc(pool.getId()).forEach(tontineMemberRepository::delete);
        int orderIndex = 1;
        for (UserEntity memberUser : membersList) {
            TontineMemberEntity memberEntity = TontineMemberEntity.builder()
                    .pool(pool)
                    .user(memberUser)
                    .orderIndex(orderIndex++)
                    .hasReceivedPayout(false)
                    .build();
            tontineMemberRepository.save(memberEntity);
        }

        int totalRounds = membersList.size();
        details.setTotalRounds(totalRounds);
        if (details.getCurrentRoundNumber() == null || details.getCurrentRoundNumber() < 1) {
            details.setCurrentRoundNumber(1);
        }
        details.setStartDate(LocalDateTime.now());
        details.setStatus(TontineStatus.ACTIVE);

        // Fixer le montant individuel = Montant global / Nombre de membres
        BigDecimal globalGoal = (pool.getGoalAmount() != null && pool.getGoalAmount().compareTo(BigDecimal.ZERO) > 0)
                ? pool.getGoalAmount()
                : (details.getContributionAmount() != null ? details.getContributionAmount().multiply(new BigDecimal(totalRounds)) : new BigDecimal("100"));
        
        BigDecimal perUserAmount = globalGoal.divide(new BigDecimal(Math.max(1, totalRounds)), 2, java.math.RoundingMode.HALF_UP);
        details.setContributionAmount(perUserAmount);
        tontineDetailsRepository.save(details);

        pool.setGoalAmount(globalGoal);
        pool.setCurrentAmount(BigDecimal.ZERO);
        poolRepository.save(pool);
    }

    private List<UserEntity> countValidMembers(PoolEntity pool) {
        List<UserEntity> membersList = new ArrayList<>();
        if (pool.getOwner() != null) {
            membersList.add(pool.getOwner());
        }

        List<PoolInvitationEntity> invitations = invitationRepository.findByPoolId(pool.getId());
        for (PoolInvitationEntity inv : invitations) {
            String email = inv.getEmail() != null ? inv.getEmail().trim() : "";
            if (!email.isEmpty()) {
                Optional<UserEntity> userOpt = userRepository.findByEmail(email);
                if (userOpt.isEmpty()) {
                    userOpt = userRepository.findAll().stream()
                            .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                            .findFirst();
                }

                UserEntity user = userOpt.orElseGet(() -> {
                    UserEntity placeholder = UserEntity.builder()
                            .fullName(email.contains("@") ? email.substring(0, email.indexOf('@')) : email)
                            .email(email.toLowerCase())
                            .password("INVITED_PENDING")
                            .status(com.alphateckplus.potify.data_jpa.entity.user.UserStatus.PENDING_VERIFICATION)
                            .enabled(false)
                            .accountNonLocked(true)
                            .build();
                    return userRepository.save(placeholder);
                });

                if (!membersList.contains(user)) {
                    membersList.add(user);
                }
            }
        }

        if (pool.getInvitedUserIds() != null && !pool.getInvitedUserIds().isBlank()) {
            String[] ids = pool.getInvitedUserIds().split(",");
            for (String id : ids) {
                String cleanId = id.trim();
                if (!cleanId.isEmpty()) {
                    userRepository.findById(cleanId).ifPresent(u -> {
                        if (!membersList.contains(u)) {
                            membersList.add(u);
                        }
                    });
                }
            }
        }
        return membersList;
    }

    public boolean isInvitationsLocked(String poolId) {
        TontineDetailsEntity details = tontineDetailsRepository.findByPoolId(poolId).orElse(null);
        if (details == null) return false;
        Integer activeRoundNum = details.getCurrentRoundNumber() != null ? details.getCurrentRoundNumber() : 1;
        return contributionRepository.countConfirmedByPoolIdAndRoundNumber(poolId, activeRoundNum) > 0;
    }

    @Transactional
    public TontineSummaryResponse getTontineSummary(String poolId) {
        PoolEntity pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte introuvable : " + poolId));

        TontineDetailsEntity details = tontineDetailsRepository.findByPoolId(poolId).orElse(null);
        if (details == null) {
            BigDecimal amount = pool.getGoalAmount() != null && pool.getGoalAmount().compareTo(BigDecimal.ZERO) > 0 
                    ? pool.getGoalAmount() : new BigDecimal("100");
            details = TontineDetailsEntity.builder()
                    .pool(pool)
                    .contributionAmount(amount)
                    .frequency(TontineFrequency.MONTHLY)
                    .latePenaltyRate(BigDecimal.ZERO)
                    .status(TontineStatus.DRAFT)
                    .currentRoundNumber(1)
                    .build();
            details = tontineDetailsRepository.save(details);
        }

        // Si la tontine est encore en DRAFT, démarrer immédiatement !
        if (details.getStatus() == TontineStatus.DRAFT) {
            startTontineInternal(pool, details);
        }

        final Integer activeRoundNum = details.getCurrentRoundNumber() != null ? details.getCurrentRoundNumber() : 1;
        
        // Synchroniser les membres s'il n'y a pas encore eu de cotisation dans le tour actif
        long paymentsInCurrentRound = contributionRepository.countConfirmedByPoolIdAndRoundNumber(poolId, activeRoundNum);
        List<TontineMemberEntity> membersEntities = tontineMemberRepository.findByPoolIdOrderByOrderIndexAsc(poolId);
        
        if (paymentsInCurrentRound == 0) {
            List<UserEntity> validMembers = countValidMembers(pool);
            if (validMembers.isEmpty() && pool.getOwner() != null) {
                validMembers.add(pool.getOwner());
            }
            List<String> validUserIds = validMembers.stream().map(UserEntity::getId).toList();
            List<String> existingUserIds = membersEntities.stream().map(m -> m.getUser().getId()).toList();

            if (!existingUserIds.equals(validUserIds)) {
                tontineMemberRepository.findByPoolIdOrderByOrderIndexAsc(poolId).forEach(tontineMemberRepository::delete);
                int orderIndex = 1;
                for (UserEntity memberUser : validMembers) {
                    TontineMemberEntity memberEntity = TontineMemberEntity.builder()
                            .pool(pool)
                            .user(memberUser)
                            .orderIndex(orderIndex++)
                            .hasReceivedPayout(false)
                            .build();
                    tontineMemberRepository.save(memberEntity);
                }
                membersEntities = tontineMemberRepository.findByPoolIdOrderByOrderIndexAsc(poolId);
                details.setTotalRounds(membersEntities.size());
                tontineDetailsRepository.save(details);
            }
        }

        int totalMembers = Math.max(1, membersEntities.size());
        details.setTotalRounds(totalMembers);

        // Règle: Montant à payer par utilisateur = Montant global fixe de la cagnotte / Nombre total de membres
        BigDecimal globalGoal = (pool.getGoalAmount() != null && pool.getGoalAmount().compareTo(BigDecimal.ZERO) > 0)
                ? pool.getGoalAmount()
                : (details.getContributionAmount() != null ? details.getContributionAmount().multiply(new BigDecimal(totalMembers)) : new BigDecimal("100"));

        BigDecimal contributionAmountPerUser = globalGoal.divide(new BigDecimal(totalMembers), 2, java.math.RoundingMode.HALF_UP);
        details.setContributionAmount(contributionAmountPerUser);
        tontineDetailsRepository.save(details);

        // L'objectif pour chaque tour est TOUJOURS le montant global fixe
        BigDecimal targetPerRound = globalGoal;
        if (pool.getGoalAmount() == null || pool.getGoalAmount().compareTo(targetPerRound) != 0) {
            pool.setGoalAmount(targetPerRound);
            poolRepository.save(pool);
        }

        List<TontineMemberDto> memberDtos = membersEntities.stream().map(m -> {
            boolean paidCurrent = contributionRepository.existsByPoolIdAndUserIdOrEmailAndRoundNumber(
                    poolId, m.getUser().getId(), m.getUser().getEmail(), activeRoundNum);
            return TontineMemberDto.builder()
                    .userId(m.getUser().getId())
                    .userName(m.getUser().getFullName() != null ? m.getUser().getFullName() : m.getUser().getEmail())
                    .userEmail(m.getUser().getEmail())
                    .orderIndex(m.getOrderIndex())
                    .hasReceivedPayout(m.getHasReceivedPayout())
                    .hasPaidCurrentRound(paidCurrent)
                    .build();
        }).toList();

        // Calcul dynamique des tours (allRounds)
        int totalRounds = totalMembers;
        LocalDateTime startDate = details.getStartDate() != null ? details.getStartDate() : LocalDateTime.now();
        Integer currentRoundNum = details.getCurrentRoundNumber() != null ? details.getCurrentRoundNumber() : 1;
        LocalDateTime now = LocalDateTime.now();

        List<TontineRoundDto> roundDtos = new ArrayList<>();
        for (int r = 1; r <= totalRounds; r++) {
            LocalDateTime dueDate = calculateDueDateForRound(startDate, details.getFrequency(), r);
            UserEntity beneficiary = (r <= membersEntities.size()) ? membersEntities.get(r - 1).getUser() : pool.getOwner();

            BigDecimal collected = contributionRepository.sumAmountByPoolIdAndRoundNumber(poolId, r);
            BigDecimal penalties = contributionRepository.sumPenaltyByPoolIdAndRoundNumber(poolId, r);

            TontineRoundStatus status;
            if (r < currentRoundNum || (details.getStatus() == TontineStatus.COMPLETED)) {
                status = TontineRoundStatus.COMPLETED;
            } else if (r == currentRoundNum) {
                if (collected.compareTo(targetPerRound) >= 0) {
                    status = TontineRoundStatus.COMPLETED;
                } else if (now.isAfter(dueDate)) {
                    status = TontineRoundStatus.OVERDUE;
                } else {
                    status = TontineRoundStatus.PENDING;
                }
            } else {
                status = TontineRoundStatus.PENDING;
            }

            TontineRoundDto dto = TontineRoundDto.builder()
                    .id("ROUND-" + poolId + "-" + r)
                    .roundNumber(r)
                    .beneficiaryId(beneficiary != null ? beneficiary.getId() : null)
                    .beneficiaryName(beneficiary != null && beneficiary.getFullName() != null ? beneficiary.getFullName() : (beneficiary != null ? beneficiary.getEmail() : "Bénéficiaire"))
                    .dueDate(dueDate)
                    .targetAmount(targetPerRound)
                    .collectedAmount(collected)
                    .collectedPenalties(penalties)
                    .status(status)
                    .payoutDate(status == TontineRoundStatus.COMPLETED ? dueDate : null)
                    .payoutTransactionId(null)
                    .build();

            roundDtos.add(dto);
        }

        TontineRoundDto activeRoundDto = roundDtos.stream()
                .filter(r -> r.getRoundNumber().equals(currentRoundNum))
                .findFirst().orElse(null);

        boolean invitationsLocked = paymentsInCurrentRound > 0;
        int resetCount = Math.max(0, currentRoundNum - 1);

        return TontineSummaryResponse.builder()
                .poolId(poolId)
                .poolTitle(pool.getTitle())
                .contributionAmount(contributionAmountPerUser)
                .frequency(details.getFrequency())
                .latePenaltyRate(details.getLatePenaltyRate())
                .totalRounds(totalRounds)
                .currentRoundNumber(details.getCurrentRoundNumber())
                .status(details.getStatus())
                .startDate(details.getStartDate())
                .members(memberDtos)
                .activeRound(activeRoundDto)
                .allRounds(roundDtos)
                .invitationsLocked(invitationsLocked)
                .resetCount(resetCount)
                .build();
    }

    @Transactional
    public TontineSummaryResponse processTontineContribution(String poolId, String userId, String paymentMethod) {
        PoolEntity pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte introuvable : " + poolId));

        TontineDetailsEntity details = tontineDetailsRepository.findByPoolId(poolId)
                .orElseThrow(() -> new IllegalStateException("La tontine n'est pas configurée."));

        if (details.getStatus() != TontineStatus.ACTIVE) {
            throw new IllegalStateException("La tontine n'est pas active.");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + userId));

        Integer currentRoundNumber = details.getCurrentRoundNumber() != null ? details.getCurrentRoundNumber() : 1;

        // Vérifier si l'utilisateur a déjà contribué à ce tour
        boolean alreadyContributed = contributionRepository.existsByPoolIdAndUserIdOrEmailAndRoundNumber(poolId, userId, user.getEmail(), currentRoundNumber);
        if (alreadyContributed) {
            throw new IllegalStateException("Vous avez déjà payé votre cotisation pour ce tour (" + currentRoundNumber + ").");
        }

        BigDecimal currentCollectedForRound = contributionRepository.sumAmountByPoolIdAndRoundNumber(poolId, currentRoundNumber);
        BigDecimal targetPerRound = pool.getGoalAmount() != null && pool.getGoalAmount().compareTo(BigDecimal.ZERO) > 0 
                ? pool.getGoalAmount() 
                : details.getContributionAmount().multiply(new BigDecimal(details.getTotalRounds()));

        if (targetPerRound.compareTo(BigDecimal.ZERO) > 0 && currentCollectedForRound.compareTo(targetPerRound) >= 0) {
            throw new IllegalStateException("L'objectif financier de ce tour (" + targetPerRound + " €) a déjà été atteint. Les cotisations sont fermées.");
        }

        LocalDateTime startDate = details.getStartDate() != null ? details.getStartDate() : LocalDateTime.now();
        LocalDateTime dueDate = calculateDueDateForRound(startDate, details.getFrequency(), currentRoundNumber);

        // Calcul des pénalités si en retard
        BigDecimal penaltyAmount = BigDecimal.ZERO;
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(dueDate) && details.getLatePenaltyRate() != null && details.getLatePenaltyRate().compareTo(BigDecimal.ZERO) > 0) {
            long daysLate = Math.max(1, Duration.between(dueDate, now).toDays());
            BigDecimal rate = details.getLatePenaltyRate().divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP);
            penaltyAmount = details.getContributionAmount().multiply(rate).multiply(new BigDecimal(daysLate)).setScale(2, java.math.RoundingMode.HALF_UP);
        }

        BigDecimal totalAmountPaid = details.getContributionAmount().add(penaltyAmount);

        // Enregistrer la contribution
        CagnotteWalletEntity wallet = walletRepository.findByPoolId(poolId).orElse(null);
        ContributionEntity contribution = ContributionEntity.builder()
                .pool(pool)
                .user(user)
                .wallet(wallet)
                .amount(details.getContributionAmount())
                .penaltyAmount(penaltyAmount)
                .roundNumber(currentRoundNumber)
                .contributorName(user.getFullName() != null ? user.getFullName() : user.getEmail())
                .contributorEmail(user.getEmail())
                .paymentMethod(paymentMethod != null ? paymentMethod : "CARD")
                .status(ContributionStatus.CONFIRMED)
                .anonymous(false)
                .fees(BigDecimal.ZERO)
                .build();
        contributionRepository.save(contribution);

        // Mettre à jour le montant collecté pour le tour actuel
        pool.setCurrentAmount(pool.getCurrentAmount().add(totalAmountPaid));
        poolRepository.save(pool);

        if (wallet != null) {
            BigDecimal currentBalance = wallet.getAvailableBalance() != null ? wallet.getAvailableBalance() : BigDecimal.ZERO;
            wallet.setAvailableBalance(currentBalance.add(totalAmountPaid));
            walletRepository.save(wallet);
        }

        // Vérification du déblocage automatique du pot pour ce tour (PAYOUT)
        BigDecimal totalCollectedForRound = contributionRepository.sumAmountByPoolIdAndRoundNumber(poolId, currentRoundNumber);

        if (totalCollectedForRound.compareTo(targetPerRound) >= 0) {
            List<TontineMemberEntity> members = tontineMemberRepository.findByPoolIdOrderByOrderIndexAsc(poolId);
            UserEntity beneficiary = (currentRoundNumber <= members.size()) ? members.get(currentRoundNumber - 1).getUser() : pool.getOwner();
            executeAutomaticPayout(pool, details, currentRoundNumber, targetPerRound, beneficiary);
        }

        return getTontineSummary(poolId);
    }

    private void executeAutomaticPayout(PoolEntity pool, TontineDetailsEntity details, Integer roundNumber, BigDecimal targetAmount, UserEntity beneficiary) {
        log.info("Objectif du tour {} atteint pour la tontine {} ! Déclenchement du Payout automatique.", roundNumber, pool.getId());

        BigDecimal totalPenalties = contributionRepository.sumPenaltyByPoolIdAndRoundNumber(pool.getId(), roundNumber);
        BigDecimal totalPayoutAmount = targetAmount.add(totalPenalties);

        CagnotteWalletEntity wallet = walletRepository.findByPoolId(pool.getId()).orElse(null);

        PayoutEntity payout = PayoutEntity.builder()
                .wallet(wallet)
                .user(beneficiary)
                .amount(totalPayoutAmount)
                .status(PayoutStatus.COMPLETED)
                .build();
        payoutRepository.save(payout);

        // Marquer le membre bénéficiaire
        tontineMemberRepository.findByPoolIdAndUserId(pool.getId(), beneficiary.getId()).ifPresent(m -> {
            m.setHasReceivedPayout(true);
            tontineMemberRepository.save(m);
        });

        // RÉINITIALISATION POUR LE PROCHAIN TOUR: Remettre currentAmount à 0 €
        pool.setCurrentAmount(BigDecimal.ZERO);

        // Avancer au tour suivant ou clôturer la tontine
        if (details.getCurrentRoundNumber() < details.getTotalRounds()) {
            details.setCurrentRoundNumber(details.getCurrentRoundNumber() + 1);
            tontineDetailsRepository.save(details);
            poolRepository.save(pool);
            log.info("Passage au tour {} (Réinitialisation n°{}) pour la tontine {} avec montant réinitialisé à 0 €.", 
                    details.getCurrentRoundNumber(), details.getCurrentRoundNumber() - 1, pool.getId());
        } else {
            details.setStatus(TontineStatus.COMPLETED);
            tontineDetailsRepository.save(details);
            pool.setStatus(CagnotteStatus.CLOTUREE);
            poolRepository.save(pool);
            log.info("Tontine {} entièrement terminée avec succès !", pool.getId());
        }
    }

    private LocalDateTime calculateDueDateForRound(LocalDateTime startDate, TontineFrequency frequency, int roundNumber) {
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
        LocalDateTime current = startDate;
        for (int i = 1; i <= roundNumber; i++) {
            current = calculateNextDueDate(current, frequency);
        }
        return current;
    }

    private LocalDateTime calculateNextDueDate(LocalDateTime from, TontineFrequency frequency) {
        if (frequency == null) return from.plusMonths(1);
        return switch (frequency) {
            case DAILY -> from.plusDays(1);
            case WEEKLY -> from.plusWeeks(1);
            case BIWEEKLY -> from.plusWeeks(2);
            case MONTHLY -> from.plusMonths(1);
            case YEARLY -> from.plusYears(1);
        };
    }
}
