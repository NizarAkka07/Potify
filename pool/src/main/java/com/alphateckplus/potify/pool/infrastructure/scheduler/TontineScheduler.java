package com.alphateckplus.potify.pool.infrastructure.scheduler;

import com.alphateckplus.potify.data_jpa.entity.pool.TontineDetailsEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.TontineFrequency;
import com.alphateckplus.potify.data_jpa.entity.pool.TontineMemberEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.TontineStatus;
import com.alphateckplus.potify.data_jpa.repository.payment.ContributionEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.TontineDetailsEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.TontineMemberEntityRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TontineScheduler {

    private final TontineDetailsEntityRepository tontineDetailsRepository;
    private final TontineMemberEntityRepository tontineMemberRepository;
    private final ContributionEntityRepository contributionRepository;

    @Scheduled(cron = "0 0 * * * *") // Exécution chaque heure
    @Transactional
    public void checkOverdueRoundsAndRemind() {
        log.info("Vérification planifiée des tours de tontine en retard...");

        List<TontineDetailsEntity> activeTontines = tontineDetailsRepository.findByStatus(TontineStatus.ACTIVE);
        LocalDateTime now = LocalDateTime.now();

        for (TontineDetailsEntity details : activeTontines) {
            Integer currentRoundNum = details.getCurrentRoundNumber() != null ? details.getCurrentRoundNumber() : 1;
            LocalDateTime startDate = details.getStartDate() != null ? details.getStartDate() : now;
            LocalDateTime dueDate = calculateDueDateForRound(startDate, details.getFrequency(), currentRoundNum);

            if (now.isAfter(dueDate)) {
                log.warn("Le tour {} de la tontine {} a atteint son échéance du {}.",
                        currentRoundNum, details.getPool().getId(), dueDate);

                BigDecimal totalCollected = contributionRepository.sumAmountByPoolIdAndRoundNumber(details.getPool().getId(), currentRoundNum);
                BigDecimal target = details.getPool().getGoalAmount() != null ? details.getPool().getGoalAmount() : details.getContributionAmount().multiply(new BigDecimal(details.getTotalRounds()));

                // Si l'objectif a été atteint ou si la période est échue, avancer au tour suivant
                if (totalCollected.compareTo(target) >= 0) {
                    if (details.getCurrentRoundNumber() < details.getTotalRounds()) {
                        details.setCurrentRoundNumber(details.getCurrentRoundNumber() + 1);
                        details.getPool().setCurrentAmount(BigDecimal.ZERO);
                        tontineDetailsRepository.save(details);
                        log.info("Réinitialisation automatique : Passage au tour {} pour la tontine {}.", details.getCurrentRoundNumber(), details.getPool().getId());
                    } else {
                        details.setStatus(TontineStatus.COMPLETED);
                        tontineDetailsRepository.save(details);
                        log.info("Tontine {} terminée avec succès.", details.getPool().getId());
                    }
                } else {
                    // Identifier les membres n'ayant pas encore cotisé pour ce tour
                    List<TontineMemberEntity> members = tontineMemberRepository.findByPoolIdOrderByOrderIndexAsc(details.getPool().getId());
                    for (TontineMemberEntity member : members) {
                        boolean paid = contributionRepository.existsByPoolIdAndUserIdOrEmailAndRoundNumber(
                                details.getPool().getId(), member.getUser().getId(), member.getUser().getEmail(), currentRoundNum);

                        if (!paid) {
                            log.info("Relance automatique pour l'utilisateur {} pour le tour {} de la tontine {}.",
                                    member.getUser().getEmail(), currentRoundNum, details.getPool().getTitle());
                        }
                    }
                }
            }
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
