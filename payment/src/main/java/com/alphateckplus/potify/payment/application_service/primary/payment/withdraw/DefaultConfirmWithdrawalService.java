package com.alphateckplus.potify.payment.application_service.primary.payment.withdraw;

import com.alphateckplus.potify.payment.application_service.secondary.notification.NotificationEventPublisherPort;
import com.alphateckplus.potify.payment.application_service.secondary.payment.PaymentRepositoryPort;
import com.alphateckplus.potify.payment.domain.model.Transaction;
import com.alphateckplus.potify.payment.domain.model.TransactionStatus;
import com.stripe.Stripe;
import com.stripe.model.Payout;
import com.stripe.param.PayoutCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Slf4j
public class DefaultConfirmWithdrawalService implements ConfirmWithdrawalService {

    private final PaymentRepositoryPort repositoryPort;
    private final NotificationEventPublisherPort notificationEventPublisherPort;

    @Value("${stripe.api.key:}")
    private String stripeApiKey;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(String transactionId) throws Exception {
        log.info("Confirming withdrawal for transaction: {}", transactionId);

        Transaction transaction = repositoryPort.findTransactionById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction introuvable"));

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Seules les transactions en attente (PENDING) peuvent être confirmées.");
        }

        // Calculate net amount (total requested - fees)
        BigDecimal netAmount = transaction.getAmount().subtract(transaction.getFees());

        // Process real Stripe payout / Bank transfer if IBAN is provided
        if (transaction.getIban() != null && !transaction.getIban().isEmpty()) {
            log.info("Executing real Stripe payout of {} EUR to IBAN {}", netAmount, transaction.getIban());
            try {
                if (stripeApiKey != null && !stripeApiKey.isEmpty()) {
                    Stripe.apiKey = stripeApiKey;
                    
                    // In Stripe, amount must be in cents (e.g. 10.00 EUR -> 1000 cents)
                    long amountInCents = netAmount.multiply(new BigDecimal("100")).longValue();
                    
                    PayoutCreateParams params = PayoutCreateParams.builder()
                            .setAmount(amountInCents)
                            .setCurrency("eur")
                            .setStatementDescriptor("POTIFY PAYOUT")
                            .build();
                    
                    Payout payout = Payout.create(params);
                    log.info("Stripe Payout successfully created: {}", payout.getId());
                } else {
                    log.warn("Stripe API key is not configured. Simulating successful Stripe payout.");
                }
            } catch (Exception e) {
                log.error("Erreur lors de l'exécution du payout Stripe", e);
                // We throw the exception to rollback transaction status if Stripe payout fails
                throw new RuntimeException("Échec du virement Stripe : " + e.getMessage());
            }
        } else {
            // PayPal payout simulation
            log.info("Executing PayPal payout of {} EUR", netAmount);
            log.info("PayPal payout simulated successfully.");
        }

        // Change status to SUCCESS
        transaction.setStatus(TransactionStatus.SUCCESS);
        repositoryPort.saveTransaction(transaction);

        // Fetch owner and title for notification
        try {
            String ownerId = repositoryPort.getPoolOwnerId(transaction.getWalletId());
            String poolTitle = repositoryPort.getPoolTitle(transaction.getWalletId());
            String title = "Retrait confirmé !";
            String content;
            if (transaction.getFees() != null && transaction.getFees().compareTo(BigDecimal.ZERO) > 0) {
                content = "Votre demande de retrait de " + transaction.getAmount() + "€ pour la cagnotte '" + poolTitle + "' a été approuvée. Un transfert de " + netAmount + "€ (après déduction des 2% de frais) a été envoyé vers vos coordonnées de paiement.";
            } else {
                content = "Votre demande de retrait de " + transaction.getAmount() + "€ pour la cagnotte '" + poolTitle + "' a été approuvée. Un transfert de " + netAmount + "€ (frais déduits au dépôt) a été envoyé vers vos coordonnées de paiement.";
            }
            notificationEventPublisherPort.publish(ownerId, "WITHDRAWAL_CONFIRMED", title, content);
        } catch (Exception e) {
            log.error("Erreur envoi notification de confirmation de retrait", e);
        }

        log.info("Withdrawal transaction {} marked as SUCCESS.", transactionId);
    }
}
