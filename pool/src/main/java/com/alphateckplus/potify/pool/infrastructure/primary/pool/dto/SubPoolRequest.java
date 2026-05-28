package com.alphateckplus.potify.pool.infrastructure.primary.pool.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SubPoolRequest(
    String title,
    String description,
    Boolean hasDeadline,
    LocalDateTime deadlineDate,
    List<PhaseRequest> phases
) {}
