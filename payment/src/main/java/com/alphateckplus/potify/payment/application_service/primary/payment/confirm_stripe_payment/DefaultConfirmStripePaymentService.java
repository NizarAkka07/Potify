package com.alphateckplus.potify.payment.application_service.primary.payment.confirm_stripe_payment;

import com.alphateckplus.potify.payment.application_service.secondary.notification.NotificationEventPublisherPort;
import com.alphateckplus.potify.payment.application_service.secondary.payment.PaymentRepositoryPort;
import com.alphateckplus.potify.payment.application_service.secondary.payment.StripeGatewayPort;
import com.alphateckplus.potify.payment.domain.model.Contribution;
import com.alphateckplus.potify.payment.domain.model.ContributionStatus;
import com.alphateckplus.potify.payment.domain.model.Transaction;
import com.alphateckplus.potify.payment.domain.model.TransactionStatus;
import com.alphateckplus.potify.payment.domain.model.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Slf4j
public class DefaultConfirmStripePaymentService implements ConfirmStripePaymentService {

    private final PaymentRepositoryPort repositoryPort;
    private final StripeGatewayPort stripeGatewayPort;
    private final NotificationEventPublisherPort notificationEventPublisherPort;

    @Override
    @Transactional
    public void execute(String sessionId) throws Exception {
        log.info("Confirming Stripe payment for session: {}", sessionId);

        stripeGatewayPort.confirmStripePayment(sessionId);

        String stripeSessionId = sessionId.startsWith("STRIPE:") ? sessionId : "STRIPE:" + sessionId;
        Contribution contribution = repositoryPort.findContributionByPaymentMethod(stripeSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Contribution introuvable pour la session Stripe: " + sessionId));

        if (contribution.getStatus() == ContributionStatus.CONFIRMED) {
            log.info("Stripe payment already confirmed for contribution: {}", contribution.getId());
            return;
        }

        processSuccessfulPayment(contribution);
    }

    private void processSuccessfulPayment(Contribution contribution) {
        BigDecimal amount = contribution.getAmount();
        BigDecimal poolFeesPercent = repositoryPort.getPoolFees(contribution.getPoolId());
        BigDecimal feeMultiplier = poolFeesPercent.divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP);
        BigDecimal fees = amount.multiply(feeMultiplier).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal netAmount = amount.subtract(fees);

        contribution.setStatus(ContributionStatus.CONFIRMED);
        contribution.setFees(fees);
        repositoryPort.saveContribution(contribution);

        repositoryPort.creditPoolAndWallet(contribution.getPoolId(), netAmount);

        Transaction transaction = Transaction.builder()
                .walletId(contribution.getPoolId())
                .contributionId(contribution.getId())
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .fees(fees)
                .status(TransactionStatus.SUCCESS)
                .build();
        repositoryPort.saveTransaction(transaction);

        try {
            String ownerId = repositoryPort.getPoolOwnerId(contribution.getPoolId());
            String poolTitle = repositoryPort.getPoolTitle(contribution.getPoolId());
            String contributorName = contribution.getContributorName() != null ? contribution.getContributorName() : "Un visiteur";
            String title = "Nouvelle contribution !";
            String content = contributorName + " a contribué un montant net de " + netAmount + "€ à votre cagnotte '" + poolTitle + "'.";
            notificationEventPublisherPort.publish(ownerId, "CONTRIBUTION", title, content);
        } catch (Exception e) {
            log.error("Erreur lors de la publication de la notification de contribution", e);
        }

        log.info("Payment successfully processed for contribution ID: {}. Net amount: {}, Fees: {}", contribution.getId(), netAmount, fees);
    }
}
