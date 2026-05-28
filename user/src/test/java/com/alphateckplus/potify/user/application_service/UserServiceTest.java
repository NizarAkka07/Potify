package com.alphateckplus.potify.user.application_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alphateckplus.potify.user.application_service.primary.user.create_user.DefaultCreateUserService;
import com.alphateckplus.potify.user.application_service.primary.user.update_user.DefaultUpdateUserService;
import com.alphateckplus.potify.user.application_service.secondary.notification.NotificationPort;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.UserAlreadyExistsException;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.domain.model.UserStatus;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.alphateckplus.potify.user.application_service.secondary.user.PasswordHashingPort;

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

    @Mock
    private NotificationPort notificationPort;

    @Mock
    private PasswordHashingPort passwordHashingPort;

    private DefaultCreateUserService defaultCreateUserService;
    private DefaultUpdateUserService defaultUpdateUserService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        defaultCreateUserService = new DefaultCreateUserService(userRepositoryPort, notificationPort, passwordHashingPort);
        defaultUpdateUserService = new DefaultUpdateUserService(userRepositoryPort);
    }

    @Test
    void createUserShouldThrowWhenEmailAlreadyExists() {
        // Arrange: on simule la presence d'un utilisateur avec le meme email.
        User user = User.builder()
            .fullName("Nizar Doe")
            .email("nizar@example.com")
            .password("secret123")
            .build();
        when(userRepositoryPort.existsByEmail("nizar@example.com")).thenReturn(true);

        // Act + Assert: le service doit refuser la creation.
        assertThatThrownBy(() -> defaultCreateUserService.execute(user))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessageContaining("nizar@example.com");
    }

    @Test
    void createUserShouldPersistNewUserWithPendingVerificationStatus() {
        // Arrange: commande de creation et resultat de persistence simule.
        User user = User.builder()
            .fullName("Nizar Doe")
            .email("nizar@example.com")
            .password("secret123")
            .build();
        User savedUser = User.builder()
            .id("u-1")
            .fullName("Nizar Doe")
            .email("nizar@example.com")
            .password("encodedSecret123")
            .status(UserStatus.PENDING_VERIFICATION)
            .build();

        when(userRepositoryPort.existsByEmail("nizar@example.com")).thenReturn(false);
        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);
        when(passwordHashingPort.hash("secret123")).thenReturn("encodedSecret123");

        // Act: execution du cas d'usage.
        User result = defaultCreateUserService.execute(user);

        // Assert: verification de l'etat metier et de l'appel repository.
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING_VERIFICATION);
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

        User userToUpdate = User.builder()
            .id("u-1")
            .fullName("Nouveau Nom")
            .email("nouveau@example.com")
            .password("newPass123")
            .status(UserStatus.ACTIVE)
            .build();

        when(userRepositoryPort.findById("u-1")).thenReturn(Optional.of(existingUser));
        when(userRepositoryPort.existsByEmail("nouveau@example.com")).thenReturn(false);
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = defaultUpdateUserService.execute(userToUpdate);

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

        User userToUpdate = User.builder()
            .id("u-1")
            .fullName("Nouveau Nom")
            .email("deja-pris@example.com")
            .password("newPass123")
            .status(UserStatus.SUSPENDED)
            .build();

        when(userRepositoryPort.findById("u-1")).thenReturn(Optional.of(existingUser));
        when(userRepositoryPort.existsByEmail("deja-pris@example.com")).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> defaultUpdateUserService.execute(userToUpdate))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessageContaining("deja-pris@example.com");
    }
}
