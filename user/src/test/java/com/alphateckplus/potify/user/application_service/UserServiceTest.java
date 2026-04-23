package com.alphateckplus.potify.user.application_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alphateckplus.potify.user.application_service.primary.user.create_user.DefaultCreateUserService;
import com.alphateckplus.potify.user.application_service.primary.user.update_user.DefaultUpdateUserService;
import com.alphateckplus.potify.user.application_service.primary.command.CreateUserCommand;
import com.alphateckplus.potify.user.application_service.primary.command.UpdateUserCommand;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.UserAlreadyExistsException;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.domain.model.UserStatus;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests unitaires du service applicatif user.
 *
 * <p>Ces tests valident la logique metier sans demarrer Spring,
 * ce qui les rend rapides et faciles a maintenir.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    private DefaultCreateUserService defaultCreateUserService;
    private DefaultUpdateUserService defaultUpdateUserService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        defaultCreateUserService = new DefaultCreateUserService(userRepositoryPort);
        defaultUpdateUserService = new DefaultUpdateUserService(userRepositoryPort);
    }

    @Test
    void createUserShouldThrowWhenEmailAlreadyExists() {
        // Arrange: on simule la presence d'un utilisateur avec le meme email.
        CreateUserCommand command = new CreateUserCommand("Nizar Doe", "nizar@example.com", "secret123");
        when(userRepositoryPort.existsByEmail("nizar@example.com")).thenReturn(true);

        // Act + Assert: le service doit refuser la creation.
        assertThatThrownBy(() -> defaultCreateUserService.execute(command))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessageContaining("nizar@example.com");
    }

    @Test
    void createUserShouldPersistNewUserWithActiveStatus() {
        // Arrange: commande de creation et resultat de persistence simule.
        CreateUserCommand command = new CreateUserCommand("Nizar Doe", "nizar@example.com", "secret123");
        User savedUser = User.builder()
            .id("u-1")
            .fullName("Nizar Doe")
            .email("nizar@example.com")
            .password("secret123")
            .status(UserStatus.ACTIVE)
            .build();

        when(userRepositoryPort.existsByEmail("nizar@example.com")).thenReturn(false);
        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        // Act: execution du cas d'usage.
        User result = defaultCreateUserService.execute(command);

        // Assert: verification de l'etat metier et de l'appel repository.
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getEmail()).isEqualTo("nizar@example.com");
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void updateUserShouldUpdateProfileFieldsAndStatus() {
        // Arrange: utilisateur existant avec un statut deja defini.
        User existingUser = User.builder()
            .id("u-1")
            .fullName("Ancien Nom")
            .email("ancien@example.com")
            .password("oldPass123")
            .status(UserStatus.SUSPENDED)
            .build();

        UpdateUserCommand command = new UpdateUserCommand(
            "u-1",
            "Nouveau Nom",
            "nouveau@example.com",
            "newPass123",
            UserStatus.ACTIVE
        );

        when(userRepositoryPort.findById("u-1")).thenReturn(Optional.of(existingUser));
        when(userRepositoryPort.existsByEmail("nouveau@example.com")).thenReturn(false);
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = defaultUpdateUserService.execute(command);

        // Assert: les champs de profil changent, et le statut aussi.
        assertThat(result.getFullName()).isEqualTo("Nouveau Nom");
        assertThat(result.getEmail()).isEqualTo("nouveau@example.com");
        assertThat(result.getPassword()).isEqualTo("newPass123");
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void updateUserShouldThrowWhenNewEmailAlreadyExists() {
        // Arrange
        User existingUser = User.builder()
            .id("u-1")
            .fullName("Ancien Nom")
            .email("ancien@example.com")
            .password("oldPass123")
            .status(UserStatus.ACTIVE)
            .build();

        UpdateUserCommand command = new UpdateUserCommand(
            "u-1",
            "Nouveau Nom",
            "deja-pris@example.com",
            "newPass123",
            UserStatus.SUSPENDED
        );

        when(userRepositoryPort.findById("u-1")).thenReturn(Optional.of(existingUser));
        when(userRepositoryPort.existsByEmail("deja-pris@example.com")).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> defaultUpdateUserService.execute(command))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessageContaining("deja-pris@example.com");
    }
}
