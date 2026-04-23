package com.alphateckplus.potify.user.infrastructure.secondary.permission.mapper;

import com.alphateckplus.potify.data_jpa.entity.user.PermissionEntity;
import com.alphateckplus.potify.user.domain.model.Permission;

/**
 * Mapper dedie a la conversion Permission domaine <-> JPA.
 */
public class PermissionPersistenceMapper {

    public PermissionEntity toEntity(Permission domain) {
        return PermissionEntity.builder()
            .id(domain.getId())
            .code(domain.getCode())
            .description(domain.getDescription())
            .build();
    }

    public Permission toDomain(PermissionEntity entity) {
        return Permission.builder()
            .id(entity.getId())
            .code(entity.getCode())
            .description(entity.getDescription())
            .build();
    }
}
