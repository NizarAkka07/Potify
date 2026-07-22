package com.alphateckplus.potify.user.infrastructure.primary.user.admin_verification;

import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.infrastructure.primary.user.dto.UserResponse;
import com.alphateckplus.potify.user.infrastructure.primary.user.mapper.UserRestMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST réservé aux Super Administrateurs pour la gestion
 * de la vérification/approbation administrative des comptes utilisateurs.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AdminVerificationController {

    private final UserRepositoryPort userRepositoryPort;
    private final UserRestMapper userRestMapper;

    @Data
    public static class AdminVerificationRequest {
        private boolean approved;
    }

    /**
     * Récupère tous les utilisateurs en attente de vérification par un administrateur.
     */
    @GetMapping("/pending-verification")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<UserResponse>> getPendingVerificationUsers() {
        List<UserResponse> pendingUsers = userRepositoryPort.findByAdminVerification(false)
                .stream()
                .map(userRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pendingUsers);
    }

    /**
     * Approuve la vérification administrative d'un utilisateur.
     */
    @PostMapping("/{id}/approve-verification")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserResponse> approveUserVerification(@PathVariable String id) {
        User updated = userRepositoryPort.updateAdminVerification(id, true);
        return ResponseEntity.ok(userRestMapper.toResponse(updated));
    }

    /**
     * Rejette la vérification administrative d'un utilisateur.
     */
    @PostMapping("/{id}/reject-verification")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserResponse> rejectUserVerification(@PathVariable String id) {
        User updated = userRepositoryPort.updateAdminVerification(id, false);
        return ResponseEntity.ok(userRestMapper.toResponse(updated));
    }

    /**
     * Modification directe du statut de vérification administrative d'un utilisateur.
     */
    @PutMapping("/{id}/admin-verification")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserResponse> updateAdminVerification(
            @PathVariable String id,
            @RequestBody AdminVerificationRequest request) {
        User updated = userRepositoryPort.updateAdminVerification(id, request.isApproved());
        return ResponseEntity.ok(userRestMapper.toResponse(updated));
    }
}
