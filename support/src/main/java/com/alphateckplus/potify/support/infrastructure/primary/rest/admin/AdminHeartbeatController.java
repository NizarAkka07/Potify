package com.alphateckplus.potify.support.infrastructure.primary.rest.admin;

import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.support.infrastructure.secondary.presence.AdminPresenceRegistry;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminHeartbeatController {

    private final AdminPresenceRegistry adminPresenceRegistry;
    private final UserEntityRepository userRepository;

    @PostMapping("/heartbeat")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> heartbeat(Principal principal) {
        if (principal != null && principal.getName() != null) {
            if (isUserAdmin(principal.getName())) {
                adminPresenceRegistry.registerAdminActivity(principal.getName());
                log.info("Heartbeat admin enregistré pour: {}", principal.getName());
                return ResponseEntity.ok(Map.of("status", "ok", "online", true));
            }
        }
        return ResponseEntity.ok(Map.of("status", "ignored", "online", false));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> logout(Principal principal) {
        if (principal != null && principal.getName() != null) {
            adminPresenceRegistry.unregisterAdmin(principal.getName());
            log.info("Déconnexion admin enregistrée pour: {}", principal.getName());
        }
        return ResponseEntity.ok(Map.of("status", "ok", "online", false));
    }

    private boolean isUserAdmin(String email) {
        Optional<UserEntity> userOpt = userRepository.findWithRolesByEmail(email);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(email);
        }
        if (userOpt.isEmpty()) {
            return false;
        }
        UserEntity user = userOpt.get();
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }
        return user.getRoles().stream().anyMatch(role -> {
            String name = role.getName().toUpperCase();
            return name.contains("ADMIN") || name.contains("MODERAT") || name.contains("SUPPORT");
        });
    }
}
