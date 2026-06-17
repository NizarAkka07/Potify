package com.alphateckplus.potify.payment.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
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
public class Contribution {
    private String id;
    private String poolId;
    private String userId;
    private BigDecimal amount;
    private BigDecimal fees;
    private String contributorEmail;
    private String contributorName;
    private String message;
    private boolean anonymous;
    private String paymentMethod;
    private ContributionStatus status;
    private Instant createdAt;
}
