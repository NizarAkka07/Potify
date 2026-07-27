package com.alphateckplus.potify.support.infrastructure.primary.rest.admin;

import com.alphateckplus.potify.data_jpa.entity.support.SupportCannedResponseEntity;
import com.alphateckplus.potify.support.application_service.primary.admin.get_canned_responses.GetCannedResponsesUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPPORT_AGENT') or hasAnyAuthority('ALL', 'SUPPORT_QUEUE_VIEW', 'SUPPORT_CHAT_ASSIGN')")
public class GetCannedResponsesController {

    private final GetCannedResponsesUseCase useCase;

    @GetMapping("/canned-responses")
    public ResponseEntity<List<SupportCannedResponseEntity>> getCannedResponses() {
        return ResponseEntity.ok(useCase.getCannedResponses());
    }
}
