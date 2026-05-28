package com.alphateckplus.potify.user.infrastructure.primary.user.change_password;

import com.alphateckplus.potify.user.application_service.primary.user.change_password.ChangePasswordService;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.infrastructure.primary.user.dto.ChangePasswordRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller pour le changement de mot de passe.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ChangePasswordController {

    private final ChangePasswordService changePasswordService;

    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
        @PathVariable String userId,
        @Valid @RequestBody ChangePasswordRequest request
    ) {
        changePasswordService.execute(
            User.builder()
                .id(userId)
                .password(request.newPassword())
                .build(),
            request.oldPassword()
        );

        return ResponseEntity.noContent().build();
    }
}
