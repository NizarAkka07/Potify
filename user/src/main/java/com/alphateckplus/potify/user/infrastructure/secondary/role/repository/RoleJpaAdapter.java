package com.alphateckplus.potify.user.infrastructure.secondary.role.repository;

import com.alphateckplus.potify.data_jpa.entity.user.PermissionEntity;
import com.alphateckplus.potify.data_jpa.entity.user.RoleEntity;
import com.alphateckplus.potify.data_jpa.repository.user.PermissionEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.RoleEntityRepository;
import com.alphateckplus.potify.user.application_service.secondary.role.RoleRepositoryPort;
import com.alphateckplus.potify.user.domain.model.Role;
import com.alphateckplus.potify.user.infrastructure.secondary.role.mapper.RolePersistenceMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adapter sortant qui implemente le port role via Spring Data JPA.
 */
@Transactional
public class RoleJpaAdapter implements RoleRepositoryPort {

    private final RoleEntityRepository roleEntityRepository;
    private final PermissionEntityRepository permissionEntityRepository;
    private final RolePersistenceMapper rolePersistenceMapper;

    public RoleJpaAdapter(
        RoleEntityRepository roleEntityRepository,
        PermissionEntityRepository permissionEntityRepository,
        RolePersistenceMapper rolePersistenceMapper
    ) {
        this.roleEntityRepository = roleEntityRepository;
        this.permissionEntityRepository = permissionEntityRepository;
        this.rolePersistenceMapper = rolePersistenceMapper;
    }

    @Override
    public Role save(Role role) {
        RoleEntity roleEntity = rolePersistenceMapper.toEntity(role);
        RoleEntity savedEntity = roleEntityRepository.save(roleEntity);
        return rolePersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Role> findById(String id) {
        return roleEntityRepository.findById(id)
            .map(rolePersistenceMapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleEntityRepository.findByName(name)
            .map(rolePersistenceMapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return roleEntityRepository.findAll()
            .stream()
            .map(rolePersistenceMapper::toDomain)
            .toList();
    }

    @Override
    public void deleteById(String roleId) {
        roleEntityRepository.deleteById(roleId);
    }

    @Override
    public void addPermissionToRole(String roleId, String permissionId) {
        RoleEntity roleEntity = roleEntityRepository.findById(roleId)
            .orElseThrow();
        PermissionEntity permissionEntity = permissionEntityRepository.findById(permissionId)
            .orElseThrow();

        roleEntity.getPermissions().add(permissionEntity);
        roleEntityRepository.save(roleEntity);
    }
}
