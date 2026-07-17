package com.alphateckplus.potify.user.application_service.primary.user.reset_password;

import com.alphateckplus.potify.user.application_service.secondary.user.PasswordHashingPort;
import com.alphateckplus.potify.user.application_service.secondary.user.SecurityTokenRepositoryPort;
import com.alphateckplus.potify.user.application_service.secondary.user.SecurityTokenRepositoryPort.SecurityTokenInfo;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation du cas d'utilisation "Reinitialisation du mot de passe".
 *
 * <p>Valide le token (existence + expiration), hash le nouveau mot de passe,
 * met a jour l'utilisateur et supprime le token utilise.
 */
public class DefaultResetPasswordService implements ResetPasswordService {

    private final SecurityTokenRepositoryPort securityTokenRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordHashingPort passwordHashingPort;

    public DefaultResetPasswordService(
            SecurityTokenRepositoryPort securityTokenRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            PasswordHashingPort passwordHashingPort) {
        this.securityTokenRepositoryPort = securityTokenRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.passwordHashingPort = passwordHashingPort;
    }

    @Override
    public boolean execute(String token, String newPassword) {
        // Chercher le token
        Optional<SecurityTokenInfo> tokenInfoOpt = securityTokenRepositoryPort.findByToken(token);

        if (tokenInfoOpt.isEmpty()) {
            return false;
        }

        SecurityTokenInfo tokenInfo = tokenInfoOpt.get();

        // Verifier le type
        if (!"PASSWORD_RESET".equals(tokenInfo.type())) {
            return false;
        }

        // Verifier l'expiration
        if (tokenInfo.expiryDate().isBefore(LocalDateTime.now())) {
            // Token expire, le supprimer
            securityTokenRepositoryPort.deleteByToken(token);
            return false;
        }

        // Recuperer l'utilisateur
        Optional<User> userOpt = userRepositoryPort.findById(tokenInfo.userId());
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        // Hasher et mettre a jour le mot de passe
        String hashedPassword = passwordHashingPort.hash(newPassword);
        user.setPassword(hashedPassword);

        // Reinitialiser les tentatives echouees au cas ou le compte etait verrouille
        user.resetFailedAttempts();

        userRepositoryPort.save(user);

        // Supprimer le token utilise (usage unique)
        securityTokenRepositoryPort.deleteByToken(token);

        return true;
    }
}
