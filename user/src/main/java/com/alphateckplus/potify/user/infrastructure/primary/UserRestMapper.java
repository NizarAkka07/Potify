package com.alphateckplus.potify.user.infrastructure.primary;

import com.alphateckplus.potify.user.infrastructure.primary.dto.UserResponse;
import com.alphateckplus.potify.user.domain.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper dedie au contrat HTTP pour eviter de retourner le modele domaine brut.
 */
@Component
public class UserRestMapper {

    /**
     * Conversion domaine vers DTO de sortie API.
     */
    public UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
