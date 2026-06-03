package com.alphateckplus.potify.payment.infrastructure.primary.payment.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CheckoutRequest {
    private UUID poolId;
    private UUID userId;
    private BigDecimal amount;
    private String contributorEmail;
    private String contributorName;
    private String message;
    private boolean anonymous;
}
