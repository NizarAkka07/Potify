package com.alphateckplus.potify.user.infrastructure.secondary.user;

import com.alphateckplus.potify.data_jpa.entity.user.RoleEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.user.RoleEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.model.Role;
import com.alphateckplus.potify.user.domain.model.User;
import com.alphateckplus.potify.user.infrastructure.secondary.role.mapper.RolePersistenceMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adapter sortant qui implemente le port repository via Spring Data JPA.
 */
@Transactional
public class UserJpaAdapter implements UserRepositoryPort {

    private final UserEntityRepository userEntityRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final UserPersistenceMapper userPersistenceMapper;
    private final RolePersistenceMapper rolePersistenceMapper;

    public UserJpaAdapter(
        UserEntityRepository userEntityRepository,
        RoleEntityRepository roleEntityRepository,
        UserPersistenceMapper userPersistenceMapper,
        RolePersistenceMapper rolePersistenceMapper
    ) {
        this.userEntityRepository = userEntityRepository;
        this.roleEntityRepository = roleEntityRepository;
        this.userPersistenceMapper = userPersistenceMapper;
        this.rolePersistenceMapper = rolePersistenceMapper;
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

    @Override
    public void addRoleToUser(String userId, String roleId) {
        UserEntity userEntity = userEntityRepository.findById(userId)
            .orElseThrow();
        RoleEntity roleEntity = roleEntityRepository.findById(roleId)
            .orElseThrow();

        userEntity.getRoles().add(roleEntity);
        userEntityRepository.save(userEntity);
    }

    @Override
    public List<Role> findRolesByUserId(String userId) {
        return userEntityRepository.findById(userId)
            .map(UserEntity::getRoles)
            .orElseGet(java.util.Set::of)
            .stream()
            .map(rolePersistenceMapper::toDomain)
            .toList();
    }
}
