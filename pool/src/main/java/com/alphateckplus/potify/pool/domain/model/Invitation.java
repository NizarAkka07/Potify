package com.alphateckplus.potify.pool.domain.model;

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
public class Invitation {
    private String id;
    private String poolId;
    private String email;
    private String status;
    private String token;
}
