package com.alphateckplus.potify.payment.application_service.secondary.payment;

import com.alphateckplus.potify.payment.domain.model.Contribution;
import com.alphateckplus.potify.payment.domain.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepositoryPort {
    Contribution saveContribution(Contribution contribution);
    Optional<Contribution> findContributionById(String id);
    Optional<Contribution> findContributionByPayPalOrderId(String orderId);
    List<Contribution> findContributionsByPoolId(String poolId);
    List<Contribution> findContributionsByUserId(String userId);
    Transaction saveTransaction(Transaction transaction);
    List<Transaction> findAllTransactions();
    void creditPoolAndWallet(String poolId, BigDecimal amount);
}
