package com.alphateckplus.potify.user.infrastructure.primary.user.list_users;

import com.alphateckplus.potify.user.application_service.primary.user.list_users.ListUsersService;
import com.alphateckplus.potify.user.infrastructure.primary.user.dto.UserResponse;
import com.alphateckplus.potify.user.infrastructure.primary.user.mapper.UserRestMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller pour le listing des utilisateurs.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ListUsersController {

    private final ListUsersService listUsersService;
    private final UserRestMapper userRestMapper;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<List<UserResponse>> listUsers() {
        List<UserResponse> users = listUsersService.execute()
            .stream()
            .map(userRestMapper::toResponse)
            .toList();

        return ResponseEntity.ok(users);
    }
}
