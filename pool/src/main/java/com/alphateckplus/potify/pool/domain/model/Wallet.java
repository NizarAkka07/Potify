package com.alphateckplus.potify.pool.domain.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Portefeuille technique d'une cagnotte.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    private String id;
    private String poolId;
    private BigDecimal availableBalance;
    private BigDecimal pendingBalance;

    public static Wallet createEmpty(String poolId) {
        return Wallet.builder()
                .poolId(poolId)
                .availableBalance(BigDecimal.ZERO)
                .pendingBalance(BigDecimal.ZERO)
                .build();
    }
}
