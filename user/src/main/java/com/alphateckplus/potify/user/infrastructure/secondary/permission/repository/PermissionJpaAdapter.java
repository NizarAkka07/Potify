package com.alphateckplus.potify.user.infrastructure.secondary.permission.repository;

import com.alphateckplus.potify.data_jpa.entity.user.PermissionEntity;
import com.alphateckplus.potify.data_jpa.repository.user.PermissionEntityRepository;
import com.alphateckplus.potify.user.application_service.secondary.permission.PermissionRepositoryPort;
import com.alphateckplus.potify.user.domain.model.Permission;
import com.alphateckplus.potify.user.infrastructure.secondary.permission.mapper.PermissionPersistenceMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adapter sortant qui implemente le port permission via Spring Data JPA.
 */
@Transactional
public class PermissionJpaAdapter implements PermissionRepositoryPort {

    private final PermissionEntityRepository permissionEntityRepository;
    private final PermissionPersistenceMapper permissionPersistenceMapper;

    public PermissionJpaAdapter(
        PermissionEntityRepository permissionEntityRepository,
        PermissionPersistenceMapper permissionPersistenceMapper
    ) {
        this.permissionEntityRepository = permissionEntityRepository;
        this.permissionPersistenceMapper = permissionPersistenceMapper;
    }

    @Override
    public Permission save(Permission permission) {
        PermissionEntity permissionEntity = permissionPersistenceMapper.toEntity(permission);
        PermissionEntity savedEntity = permissionEntityRepository.save(permissionEntity);
        return permissionPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Permission> findById(String id) {
        return permissionEntityRepository.findById(id)
            .map(permissionPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByCode(String code) {
        return permissionEntityRepository.findByCode(code)
            .map(permissionPersistenceMapper::toDomain);
    }

    @Override
    public List<Permission> findAll() {
        return permissionEntityRepository.findAll()
            .stream()
            .map(permissionPersistenceMapper::toDomain)
            .toList();
    }

    @Override
    public void deleteById(String permissionId) {
        permissionEntityRepository.deleteById(permissionId);
    }
}
