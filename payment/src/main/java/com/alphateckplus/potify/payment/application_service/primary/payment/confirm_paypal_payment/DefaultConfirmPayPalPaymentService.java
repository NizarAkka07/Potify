package com.alphateckplus.potify.payment.application_service.primary.payment.confirm_paypal_payment;

import com.alphateckplus.potify.payment.application_service.secondary.notification.NotificationEventPublisherPort;
import com.alphateckplus.potify.payment.application_service.secondary.payment.PayPalGatewayPort;
import com.alphateckplus.potify.payment.application_service.secondary.payment.PaymentRepositoryPort;
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
public class DefaultConfirmPayPalPaymentService implements ConfirmPayPalPaymentService {

    private final PaymentRepositoryPort repositoryPort;
    private final PayPalGatewayPort payPalGatewayPort;
    private final NotificationEventPublisherPort notificationEventPublisherPort;

    @Override
    @Transactional
    public void execute(String orderId) throws Exception {
        log.info("Capturing PayPal order for order ID: {}", orderId);

        payPalGatewayPort.confirmPayPalPayment(orderId);

        Contribution contribution = repositoryPort.findContributionByPayPalOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Contribution associée à l'ordre PayPal introuvable: " + orderId));

        if (contribution.getStatus() == ContributionStatus.CONFIRMED) {
            log.info("PayPal payment already confirmed for order: {}", orderId);
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
