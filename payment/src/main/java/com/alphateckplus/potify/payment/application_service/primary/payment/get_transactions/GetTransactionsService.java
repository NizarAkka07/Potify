package com.alphateckplus.potify.payment.application_service.primary.payment.get_transactions;

import com.alphateckplus.potify.payment.domain.model.Transaction;
import java.util.List;

public interface GetTransactionsService {
    List<Transaction> execute();
}
