package com.alphateckplus.potify.user.infrastructure.primary.user.create_user;

import com.alphateckplus.potify.user.application_service.primary.user.create_user.CreateUserService;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.infrastructure.primary.user.dto.CreateUserRequest;
import com.alphateckplus.potify.user.infrastructure.primary.user.dto.UserResponse;
import com.alphateckplus.potify.user.infrastructure.primary.user.mapper.UserRestMapper;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller pour la creation d'un utilisateur.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class CreateUserController {

    private final CreateUserService createUserService;
    private final UserRestMapper userRestMapper;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        var createdUser = createUserService.execute(User.builder()
            .fullName(request.fullName())
            .email(request.email())
            .password(request.password())
            .build());

        UserResponse body = userRestMapper.toResponse(createdUser);
        return ResponseEntity
            .created(URI.create("/api/v1/users/" + createdUser.getId()))
            .body(body);
    }
}
