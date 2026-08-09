package com.alphateckplus.potify.support.infrastructure.primary.rest.user;

import com.alphateckplus.potify.support.infrastructure.secondary.presence.AdminPresenceRegistry;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support/user")
@RequiredArgsConstructor
public class GetAdminAvailabilityController {

    private final AdminPresenceRegistry adminPresenceRegistry;

    @GetMapping("/admin-availability")
    public ResponseEntity<Map<String, Object>> getAdminAvailability() {
        boolean isOnline = adminPresenceRegistry.isAnyAdminOnline();
        int count = adminPresenceRegistry.getOnlineAdminCount();
        return ResponseEntity.ok(Map.of(
                "online", isOnline,
                "count", count
        ));
    }
}
