package com.alphateckplus.potify.pool.application_service.primary.command;

import java.time.LocalDateTime;
import java.util.List;

public record SubPoolCommand(
    String title,
    String description,
    Boolean hasDeadline,
    LocalDateTime deadlineDate,
    List<PhaseCommand> phases
) {}
