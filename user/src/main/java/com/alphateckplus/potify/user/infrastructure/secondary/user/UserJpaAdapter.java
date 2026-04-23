package com.alphateckplus.potify.user.infrastructure.secondary.user;

import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adapter sortant qui implemente le port repository via Spring Data JPA.
 */
@Transactional
public class UserJpaAdapter implements UserRepositoryPort {

    private final UserEntityRepository userEntityRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    public UserJpaAdapter(
        UserEntityRepository userEntityRepository,
        UserPersistenceMapper userPersistenceMapper
    ) {
        this.userEntityRepository = userEntityRepository;
        this.userPersistenceMapper = userPersistenceMapper;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userPersistenceMapper.toEntity(user);
        UserEntity savedEntity = userEntityRepository.save(userEntity);
        return userPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(String id) {
        return userEntityRepository.findById(id)
            .map(userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userEntityRepository.findByEmail(email)
            .map(userPersistenceMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userEntityRepository.findAll()
            .stream()
            .map(userPersistenceMapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userEntityRepository.existsByEmail(email);
    }
}
