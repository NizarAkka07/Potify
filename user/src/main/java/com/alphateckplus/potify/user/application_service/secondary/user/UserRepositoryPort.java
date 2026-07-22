package com.alphateckplus.potify.user.application_service.secondary.user;

import com.alphateckplus.potify.user.domain.model.Role;
import com.alphateckplus.potify.user.domain.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Port de sortie qui abstrait la persistence des utilisateurs.
 *
 * <p>Le domaine depend de ce contrat (abstraction), pas de JPA (implementation).
 */
public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationToken(String token);

    List<User> findAll();

    boolean existsByEmail(String email);

    void addRoleToUser(String userId, String roleId);

    void removeRoleFromUser(String userId, String roleId);

    List<Role> findRolesByUserId(String userId);

    void associateContributionsToUser(String email, String userId);

    List<User> findByAdminVerification(boolean adminVerification);

    User updateAdminVerification(String userId, boolean approved);
}
