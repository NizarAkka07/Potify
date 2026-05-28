package com.alphateckplus.potify.user.infrastructure.primary.user.get_user_by_id;

import com.alphateckplus.potify.user.application_service.primary.user.get_user_by_id.GetUserByIdService;
import com.alphateckplus.potify.user.infrastructure.primary.user.dto.UserResponse;
import com.alphateckplus.potify.user.infrastructure.primary.user.mapper.UserRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller pour la recuperation d'un utilisateur par identifiant.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class GetUserByIdController {

    private final GetUserByIdService getUserByIdService;
    private final UserRestMapper userRestMapper;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
        var user = getUserByIdService.execute(userId);
        return ResponseEntity.ok(userRestMapper.toResponse(user));
    }
}
