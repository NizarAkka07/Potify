package com.alphateckplus.potify.payment.application_service.primary.payment.withdraw;

import com.alphateckplus.potify.payment.application_service.secondary.notification.NotificationEventPublisherPort;
import com.alphateckplus.potify.payment.application_service.secondary.payment.PaymentRepositoryPort;
import com.alphateckplus.potify.payment.domain.model.Transaction;
import com.alphateckplus.potify.payment.domain.model.TransactionStatus;
import com.alphateckplus.potify.payment.domain.model.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Slf4j
public class DefaultWithdrawService implements WithdrawService {

    private final PaymentRepositoryPort repositoryPort;
    private final NotificationEventPublisherPort notificationEventPublisherPort;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(WithdrawRequest request) throws Exception {
        log.info("Processing withdrawal request for pool: {}, amount: {}", request.getPoolId(), request.getAmount());

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant du retrait doit être supérieur à 0");
        }

        // Validate that user is owner
        String ownerId = repositoryPort.getPoolOwnerId(request.getPoolId());
        if (!ownerId.equals(request.getUserId())) {
            throw new IllegalAccessException("Seul le propriétaire de la cagnotte peut effectuer un retrait");
        }

        // Calculate 2% fees
        BigDecimal feePercent = new BigDecimal("0.02");
        BigDecimal fees = request.getAmount().multiply(feePercent);

        // Debit wallet available balance (reserve funds)
        repositoryPort.debitWallet(request.getPoolId(), request.getAmount());

        // Create transaction trace with PENDING status
        Transaction transaction = Transaction.builder()
                .walletId(request.getPoolId())
                .type(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .fees(fees)
                .status(TransactionStatus.PENDING)
                .iban(request.getIban())
                .accountHolderName(request.getAccountHolderName())
                .bankName(request.getBankName())
                .build();
        
        repositoryPort.saveTransaction(transaction);

        try {
            String poolTitle = repositoryPort.getPoolTitle(request.getPoolId());
            String title = "Demande de retrait enregistrée";
            String content = "Votre demande de retrait de " + request.getAmount() + "€ (frais appliqués : " + fees + "€) pour la cagnotte '" + poolTitle + "' est en cours de traitement.";
            notificationEventPublisherPort.publish(request.getUserId(), "WITHDRAWAL_REQUESTED", title, content);
        } catch (Exception e) {
            log.error("Erreur lors de la publication de la notification de retrait", e);
        }

        log.info("Withdrawal request created with PENDING status. Amount: {}, Fees applied: {}", request.getAmount(), fees);
    }
}
