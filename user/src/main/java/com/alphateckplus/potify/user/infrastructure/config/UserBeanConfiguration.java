package com.alphateckplus.potify.user.infrastructure.config;

import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.user.application_service.primary.create_user.CreateUserService;
import com.alphateckplus.potify.user.application_service.primary.create_user.DefaultCreateUserService;
import com.alphateckplus.potify.user.application_service.primary.get_user_by_id.DefaultGetUserByIdService;
import com.alphateckplus.potify.user.application_service.primary.get_user_by_id.GetUserByIdService;
import com.alphateckplus.potify.user.application_service.primary.list_users.DefaultListUsersService;
import com.alphateckplus.potify.user.application_service.primary.list_users.ListUsersService;
import com.alphateckplus.potify.user.application_service.primary.update_user.DefaultUpdateUserService;
import com.alphateckplus.potify.user.application_service.primary.update_user.UpdateUserService;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.infrastructure.secondary.user.UserJpaAdapter;
import com.alphateckplus.potify.user.infrastructure.secondary.user.UserPersistenceMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration explicite des beans du microservice user.
 *
 * <p>Objectif: garder les classes metier sans annotations de framework et
 * centraliser le wiring Spring ici.
 */
@Configuration
public class UserBeanConfiguration {

    @Bean
    public UserPersistenceMapper userPersistenceMapper() {
        return new UserPersistenceMapper();
    }

    @Bean
    public UserRepositoryPort userRepositoryPort(
        UserEntityRepository userEntityRepository,
        UserPersistenceMapper userPersistenceMapper
    ) {
        return new UserJpaAdapter(userEntityRepository, userPersistenceMapper);
    }

    @Bean
    public CreateUserService createUserService(UserRepositoryPort userRepositoryPort) {
        return new DefaultCreateUserService(userRepositoryPort);
    }

    @Bean
    public GetUserByIdService getUserByIdService(UserRepositoryPort userRepositoryPort) {
        return new DefaultGetUserByIdService(userRepositoryPort);
    }

    @Bean
    public ListUsersService listUsersService(UserRepositoryPort userRepositoryPort) {
        return new DefaultListUsersService(userRepositoryPort);
    }

    @Bean
    public UpdateUserService updateUserService(UserRepositoryPort userRepositoryPort) {
        return new DefaultUpdateUserService(userRepositoryPort);
    }
}
