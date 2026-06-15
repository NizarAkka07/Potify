package com.alphateckplus.potify.payment.application_service.primary.payment.withdraw;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequest {
    private String poolId;
    private String userId;
    private BigDecimal amount;
    private String iban;
    private String accountHolderName;
    private String bankName;
}
