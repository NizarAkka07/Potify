package com.alphateckplus.potify.payment.domain.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String id;
    private String walletId;
    private String contributionId;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal fees;
    private TransactionStatus status;
    private String iban;
    private String accountHolderName;
    private String bankName;
}
