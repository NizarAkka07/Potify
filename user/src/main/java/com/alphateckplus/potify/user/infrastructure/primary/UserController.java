package com.alphateckplus.potify.user.infrastructure.primary;

import com.alphateckplus.potify.user.infrastructure.primary.dto.CreateUserRequest;
import com.alphateckplus.potify.user.infrastructure.primary.dto.UpdateUserRequest;
import com.alphateckplus.potify.user.infrastructure.primary.dto.UserResponse;
import com.alphateckplus.potify.user.application_service.primary.user.create_user.CreateUserService;
import com.alphateckplus.potify.user.application_service.primary.user.get_user_by_id.GetUserByIdService;
import com.alphateckplus.potify.user.application_service.primary.user.list_users.ListUsersService;
import com.alphateckplus.potify.user.application_service.primary.user.update_user.UpdateUserService;
import com.alphateckplus.potify.user.application_service.primary.command.CreateUserCommand;
import com.alphateckplus.potify.user.application_service.primary.command.UpdateUserCommand;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adapter entrant REST du microservice user.
 *
 * <p>Le controleur se limite a la couche transport (HTTP) et delegue toute
 * logique metier aux cas d'usage pour respecter SRP.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserService createUserService;
    private final GetUserByIdService getUserByIdService;
    private final ListUsersService listUsersService;
    private final UpdateUserService updateUserService;
    private final UserRestMapper userRestMapper;

    /**
     * Endpoint de creation utilisateur.
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        var createdUser = createUserService.execute(new CreateUserCommand(
            request.fullName(),
            request.email(),
            request.password()
        ));

        UserResponse body = userRestMapper.toResponse(createdUser);
        return ResponseEntity
            .created(URI.create("/api/v1/users/" + createdUser.getId()))
            .body(body);
    }

    /**
     * Endpoint de lecture d'un utilisateur par identifiant.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
        var user = getUserByIdService.execute(userId);
        return ResponseEntity.ok(userRestMapper.toResponse(user));
    }

    /**
     * Endpoint de listing des utilisateurs.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers() {
        List<UserResponse> users = listUsersService.execute()
            .stream()
            .map(userRestMapper::toResponse)
            .toList();

        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint de mise a jour complete des informations utilisateur.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
        @PathVariable String userId,
        @Valid @RequestBody UpdateUserRequest request
    ) {
        var updatedUser = updateUserService.execute(
            new UpdateUserCommand(
                userId,
                request.fullName(),
                request.email(),
                request.password(),
                request.status()
            )
        );

        return ResponseEntity.ok(userRestMapper.toResponse(updatedUser));
    }
}
