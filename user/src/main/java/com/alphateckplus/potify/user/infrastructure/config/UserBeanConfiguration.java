package com.alphateckplus.potify.user.infrastructure.config;

import com.alphateckplus.potify.data_jpa.repository.user.PermissionEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.RoleEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.user.application_service.primary.role.assign_permission_to_role.AssignPermissionToRoleService;
import com.alphateckplus.potify.user.application_service.primary.role.assign_permission_to_role.DefaultAssignPermissionToRoleService;
import com.alphateckplus.potify.user.application_service.primary.user.assign_role_to_user.AssignRoleToUserService;
import com.alphateckplus.potify.user.application_service.primary.user.assign_role_to_user.DefaultAssignRoleToUserService;
import com.alphateckplus.potify.user.application_service.primary.permission.create_permission.CreatePermissionService;
import com.alphateckplus.potify.user.application_service.primary.permission.create_permission.DefaultCreatePermissionService;
import com.alphateckplus.potify.user.application_service.primary.role.create_role.CreateRoleService;
import com.alphateckplus.potify.user.application_service.primary.role.create_role.DefaultCreateRoleService;
import com.alphateckplus.potify.user.application_service.primary.permission.delete_permission.DefaultDeletePermissionService;
import com.alphateckplus.potify.user.application_service.primary.permission.delete_permission.DeletePermissionService;
import com.alphateckplus.potify.user.application_service.primary.role.delete_role.DefaultDeleteRoleService;
import com.alphateckplus.potify.user.application_service.primary.role.delete_role.DeleteRoleService;
import com.alphateckplus.potify.user.application_service.primary.user.create_user.CreateUserService;
import com.alphateckplus.potify.user.application_service.primary.user.create_user.DefaultCreateUserService;
import com.alphateckplus.potify.user.application_service.primary.permission.get_permission_by_id.DefaultGetPermissionByIdService;
import com.alphateckplus.potify.user.application_service.primary.permission.get_permission_by_id.GetPermissionByIdService;
import com.alphateckplus.potify.user.application_service.primary.role.get_role_by_id.DefaultGetRoleByIdService;
import com.alphateckplus.potify.user.application_service.primary.role.get_role_by_id.GetRoleByIdService;
import com.alphateckplus.potify.user.application_service.primary.user.get_user_by_id.DefaultGetUserByIdService;
import com.alphateckplus.potify.user.application_service.primary.user.get_user_by_id.GetUserByIdService;
import com.alphateckplus.potify.user.application_service.primary.permission.list_permissions.DefaultListPermissionsService;
import com.alphateckplus.potify.user.application_service.primary.permission.list_permissions.ListPermissionsService;
import com.alphateckplus.potify.user.application_service.primary.role.list_roles.DefaultListRolesService;
import com.alphateckplus.potify.user.application_service.primary.role.list_roles.ListRolesService;
import com.alphateckplus.potify.user.application_service.primary.user.list_roles_by_user.DefaultListRolesByUserService;
import com.alphateckplus.potify.user.application_service.primary.user.list_roles_by_user.ListRolesByUserService;
import com.alphateckplus.potify.user.application_service.primary.user.list_users.DefaultListUsersService;
import com.alphateckplus.potify.user.application_service.primary.user.list_users.ListUsersService;
import com.alphateckplus.potify.user.application_service.primary.permission.update_permission.DefaultUpdatePermissionService;
import com.alphateckplus.potify.user.application_service.primary.permission.update_permission.UpdatePermissionService;
import com.alphateckplus.potify.user.application_service.primary.role.update_role.DefaultUpdateRoleService;
import com.alphateckplus.potify.user.application_service.primary.role.update_role.UpdateRoleService;
import com.alphateckplus.potify.user.application_service.primary.user.update_user.DefaultUpdateUserService;
import com.alphateckplus.potify.user.application_service.primary.user.update_user.UpdateUserService;
import com.alphateckplus.potify.user.application_service.secondary.permission.PermissionRepositoryPort;
import com.alphateckplus.potify.user.application_service.secondary.role.RoleRepositoryPort;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.infrastructure.secondary.permission.mapper.PermissionPersistenceMapper;
import com.alphateckplus.potify.user.infrastructure.secondary.permission.repository.PermissionJpaAdapter;
import com.alphateckplus.potify.user.infrastructure.secondary.role.mapper.RolePersistenceMapper;
import com.alphateckplus.potify.user.infrastructure.secondary.role.repository.RoleJpaAdapter;
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
    public PermissionPersistenceMapper permissionPersistenceMapper() {
        return new PermissionPersistenceMapper();
    }

    @Bean
    public RolePersistenceMapper rolePersistenceMapper(
        PermissionPersistenceMapper permissionPersistenceMapper
    ) {
        return new RolePersistenceMapper(permissionPersistenceMapper);
    }

    @Bean
    public UserPersistenceMapper userPersistenceMapper() {
        return new UserPersistenceMapper();
    }

    @Bean
    public RoleRepositoryPort roleRepositoryPort(
        RoleEntityRepository roleEntityRepository,
        PermissionEntityRepository permissionEntityRepository,
        RolePersistenceMapper rolePersistenceMapper
    ) {
        return new RoleJpaAdapter(
            roleEntityRepository,
            permissionEntityRepository,
            rolePersistenceMapper
        );
    }

    @Bean
    public PermissionRepositoryPort permissionRepositoryPort(
        PermissionEntityRepository permissionEntityRepository,
        PermissionPersistenceMapper permissionPersistenceMapper
    ) {
        return new PermissionJpaAdapter(permissionEntityRepository, permissionPersistenceMapper);
    }

    @Bean
    public UserRepositoryPort userRepositoryPort(
        UserEntityRepository userEntityRepository,
        RoleEntityRepository roleEntityRepository,
        UserPersistenceMapper userPersistenceMapper,
        RolePersistenceMapper rolePersistenceMapper
    ) {
        return new UserJpaAdapter(
            userEntityRepository,
            roleEntityRepository,
            userPersistenceMapper,
            rolePersistenceMapper
        );
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

    @Bean
    public CreateRoleService createRoleService(RoleRepositoryPort roleRepositoryPort) {
        return new DefaultCreateRoleService(roleRepositoryPort);
    }

    @Bean
    public CreatePermissionService createPermissionService(
        PermissionRepositoryPort permissionRepositoryPort
    ) {
        return new DefaultCreatePermissionService(permissionRepositoryPort);
    }

    @Bean
    public AssignRoleToUserService assignRoleToUserService(
        UserRepositoryPort userRepositoryPort,
        RoleRepositoryPort roleRepositoryPort
    ) {
        return new DefaultAssignRoleToUserService(userRepositoryPort, roleRepositoryPort);
    }

    @Bean
    public AssignPermissionToRoleService assignPermissionToRoleService(
        RoleRepositoryPort roleRepositoryPort,
        PermissionRepositoryPort permissionRepositoryPort
    ) {
        return new DefaultAssignPermissionToRoleService(
            roleRepositoryPort,
            permissionRepositoryPort
        );
    }

    @Bean
    public ListRolesByUserService listRolesByUserService(UserRepositoryPort userRepositoryPort) {
        return new DefaultListRolesByUserService(userRepositoryPort);
    }

    @Bean
    public GetRoleByIdService getRoleByIdService(RoleRepositoryPort roleRepositoryPort) {
        return new DefaultGetRoleByIdService(roleRepositoryPort);
    }

    @Bean
    public ListRolesService listRolesService(RoleRepositoryPort roleRepositoryPort) {
        return new DefaultListRolesService(roleRepositoryPort);
    }

    @Bean
    public UpdateRoleService updateRoleService(RoleRepositoryPort roleRepositoryPort) {
        return new DefaultUpdateRoleService(roleRepositoryPort);
    }

    @Bean
    public DeleteRoleService deleteRoleService(RoleRepositoryPort roleRepositoryPort) {
        return new DefaultDeleteRoleService(roleRepositoryPort);
    }

    @Bean
    public GetPermissionByIdService getPermissionByIdService(
        PermissionRepositoryPort permissionRepositoryPort
    ) {
        return new DefaultGetPermissionByIdService(permissionRepositoryPort);
    }

    @Bean
    public ListPermissionsService listPermissionsService(
        PermissionRepositoryPort permissionRepositoryPort
    ) {
        return new DefaultListPermissionsService(permissionRepositoryPort);
    }

    @Bean
    public UpdatePermissionService updatePermissionService(
        PermissionRepositoryPort permissionRepositoryPort
    ) {
        return new DefaultUpdatePermissionService(permissionRepositoryPort);
    }

    @Bean
    public DeletePermissionService deletePermissionService(
        PermissionRepositoryPort permissionRepositoryPort
    ) {
        return new DefaultDeletePermissionService(permissionRepositoryPort);
    }

    @Bean
    public com.alphateckplus.potify.user.application_service.primary.user.remove_role_from_user.RemoveRoleFromUserService removeRoleFromUserService(
        UserRepositoryPort userRepositoryPort,
        RoleRepositoryPort roleRepositoryPort
    ) {
        return new com.alphateckplus.potify.user.application_service.primary.user.remove_role_from_user.DefaultRemoveRoleFromUserService(
            userRepositoryPort, roleRepositoryPort
        );
    }

    @Bean
    public com.alphateckplus.potify.user.application_service.primary.role.remove_permission_from_role.RemovePermissionFromRoleService removePermissionFromRoleService(
        RoleRepositoryPort roleRepositoryPort,
        PermissionRepositoryPort permissionRepositoryPort
    ) {
        return new com.alphateckplus.potify.user.application_service.primary.role.remove_permission_from_role.DefaultRemovePermissionFromRoleService(
            roleRepositoryPort, permissionRepositoryPort
        );
    }
}
