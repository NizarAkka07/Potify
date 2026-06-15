package com.alphateckplus.potify.payment.application_service.primary.payment.withdraw;

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

        // Debit wallet available balance
        repositoryPort.debitWallet(request.getPoolId(), request.getAmount());

        // Create transaction trace
        Transaction transaction = Transaction.builder()
                .walletId(request.getPoolId())
                .type(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .fees(fees)
                .status(TransactionStatus.SUCCESS)
                .build();
        
        repositoryPort.saveTransaction(transaction);

        log.info("Withdrawal successfully processed. Amount: {}, Fees applied: {}", request.getAmount(), fees);
    }
}
