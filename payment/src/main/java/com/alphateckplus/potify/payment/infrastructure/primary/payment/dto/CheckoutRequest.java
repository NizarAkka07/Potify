package com.alphateckplus.potify.payment.infrastructure.primary.payment.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CheckoutRequest {
    private String poolId;
    private String userId;
    private BigDecimal amount;
    private String contributorEmail;
    private String contributorName;
    private String message;
    private boolean anonymous;
}
