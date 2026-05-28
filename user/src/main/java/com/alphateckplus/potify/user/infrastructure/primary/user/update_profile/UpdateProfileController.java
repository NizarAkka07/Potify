package com.alphateckplus.potify.user.infrastructure.primary.user.update_profile;

import com.alphateckplus.potify.user.application_service.primary.user.update_profile.UpdateProfileService;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.infrastructure.primary.user.dto.UpdateProfileRequest;
import com.alphateckplus.potify.user.infrastructure.primary.user.dto.UserResponse;
import com.alphateckplus.potify.user.infrastructure.primary.user.mapper.UserRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller pour la mise a jour du profil.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UpdateProfileController {

    private final UpdateProfileService updateProfileService;
    private final UserRestMapper userRestMapper;

    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserResponse> updateProfile(
        @PathVariable String userId,
        @Valid @RequestBody UpdateProfileRequest request
    ) {
        var updatedUser = updateProfileService.execute(
            User.builder()
                .id(userId)
                .fullName(request.fullName())
                .build()
        );

        return ResponseEntity.ok(userRestMapper.toResponse(updatedUser));
    }
}
