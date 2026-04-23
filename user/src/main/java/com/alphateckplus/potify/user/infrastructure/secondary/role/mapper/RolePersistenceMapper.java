package com.alphateckplus.potify.user.infrastructure.secondary.role.mapper;

import com.alphateckplus.potify.data_jpa.entity.user.RoleEntity;
import com.alphateckplus.potify.user.domain.model.Role;
import com.alphateckplus.potify.user.infrastructure.secondary.permission.mapper.PermissionPersistenceMapper;
import java.util.stream.Collectors;

/**
 * Mapper dedie a la conversion Role domaine <-> JPA.
 */
public class RolePersistenceMapper {

    private final PermissionPersistenceMapper permissionPersistenceMapper;

    public RolePersistenceMapper(
        PermissionPersistenceMapper permissionPersistenceMapper
    ) {
        this.permissionPersistenceMapper = permissionPersistenceMapper;
    }

    public RoleEntity toEntity(Role domain) {
        return RoleEntity.builder()
            .id(domain.getId())
            .name(domain.getName())
            .description(domain.getDescription())
            .build();
    }

    public Role toDomain(RoleEntity entity) {
        return Role.builder()
            .id(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .permissions(entity.getPermissions()
                .stream()
                .map(permissionPersistenceMapper::toDomain)
                .collect(Collectors.toSet()))
            .build();
    }
}
