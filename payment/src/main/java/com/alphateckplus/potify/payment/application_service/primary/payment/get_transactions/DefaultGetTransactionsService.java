package com.alphateckplus.potify.payment.application_service.primary.payment.get_transactions;

import com.alphateckplus.potify.payment.application_service.secondary.payment.PaymentRepositoryPort;
import com.alphateckplus.potify.payment.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class DefaultGetTransactionsService implements GetTransactionsService {

    private final PaymentRepositoryPort repositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> execute() {
        return repositoryPort.findAllTransactions();
    }
}
