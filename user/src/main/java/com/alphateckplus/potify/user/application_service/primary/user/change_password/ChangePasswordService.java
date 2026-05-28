package com.alphateckplus.potify.user.application_service.primary.user.change_password;

import com.alphateckplus.potify.user.domain.model.User;

/**
 * Port primaire pour le changement de mot de passe.
 */
public interface ChangePasswordService {
    void execute(User user, String oldPassword);
}
