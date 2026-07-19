package com.alphateckplus.potify.user.infrastructure.primary.user.update_user;

import com.alphateckplus.potify.user.application_service.primary.user.update_user.UpdateUserService;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.infrastructure.primary.user.dto.UpdateUserRequest;
import com.alphateckplus.potify.user.infrastructure.primary.user.dto.UserResponse;
import com.alphateckplus.potify.user.infrastructure.primary.user.mapper.UserRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller pour la mise a jour complete des informations utilisateur.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UpdateUserController {

    private final UpdateUserService updateUserService;
    private final UserRestMapper userRestMapper;

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_WRITE') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
        @PathVariable String userId,
        @Valid @RequestBody UpdateUserRequest request
    ) {
        var updatedUser = updateUserService.execute(
            User.builder()
                .id(userId)
                .fullName(request.fullName())
                .email(request.email())
                .password(request.password())
                .status(request.status())
                .build()
        );

        return ResponseEntity.ok(userRestMapper.toResponse(updatedUser));
    }
}
