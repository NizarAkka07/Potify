package com.alphateckplus.potify.user.application_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alphateckplus.potify.user.application_service.primary.role.assign_permission_to_role.DefaultAssignPermissionToRoleService;
import com.alphateckplus.potify.user.application_service.primary.user.assign_role_to_user.DefaultAssignRoleToUserService;
import com.alphateckplus.potify.user.application_service.primary.command.AssignPermissionToRoleCommand;
import com.alphateckplus.potify.user.application_service.primary.command.AssignRoleToUserCommand;
import com.alphateckplus.potify.user.application_service.primary.command.CreatePermissionCommand;
import com.alphateckplus.potify.user.application_service.primary.command.CreateRoleCommand;
import com.alphateckplus.potify.user.application_service.primary.command.UpdatePermissionCommand;
import com.alphateckplus.potify.user.application_service.primary.command.UpdateRoleCommand;
import com.alphateckplus.potify.user.application_service.primary.permission.create_permission.DefaultCreatePermissionService;
import com.alphateckplus.potify.user.application_service.primary.role.create_role.DefaultCreateRoleService;
import com.alphateckplus.potify.user.application_service.primary.permission.delete_permission.DefaultDeletePermissionService;
import com.alphateckplus.potify.user.application_service.primary.role.delete_role.DefaultDeleteRoleService;
import com.alphateckplus.potify.user.application_service.primary.permission.get_permission_by_id.DefaultGetPermissionByIdService;
import com.alphateckplus.potify.user.application_service.primary.role.get_role_by_id.DefaultGetRoleByIdService;
import com.alphateckplus.potify.user.application_service.primary.permission.list_permissions.DefaultListPermissionsService;
import com.alphateckplus.potify.user.application_service.primary.role.list_roles.DefaultListRolesService;
import com.alphateckplus.potify.user.application_service.primary.user.list_roles_by_user.DefaultListRolesByUserService;
import com.alphateckplus.potify.user.application_service.primary.permission.update_permission.DefaultUpdatePermissionService;
import com.alphateckplus.potify.user.application_service.primary.role.update_role.DefaultUpdateRoleService;
import com.alphateckplus.potify.user.application_service.secondary.permission.PermissionRepositoryPort;
import com.alphateckplus.potify.user.application_service.secondary.role.RoleRepositoryPort;
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
import com.alphateckplus.potify.user.domain.exception.PermissionAlreadyExistsException;
import com.alphateckplus.potify.user.domain.exception.PermissionNotFoundException;
import com.alphateckplus.potify.user.domain.exception.RoleAlreadyExistsException;
import com.alphateckplus.potify.user.domain.exception.RoleNotFoundException;
import com.alphateckplus.potify.user.domain.exception.UserNotFoundException;
import com.alphateckplus.potify.user.domain.model.Permission;
import com.alphateckplus.potify.user.domain.model.Role;
import com.alphateckplus.potify.user.domain.model.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccessManagementServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private RoleRepositoryPort roleRepositoryPort;

    @Mock
    private PermissionRepositoryPort permissionRepositoryPort;

    private DefaultCreateRoleService defaultCreateRoleService;
    private DefaultCreatePermissionService defaultCreatePermissionService;
    private DefaultAssignRoleToUserService defaultAssignRoleToUserService;
    private DefaultAssignPermissionToRoleService defaultAssignPermissionToRoleService;
    private DefaultListRolesByUserService defaultListRolesByUserService;
    private DefaultGetRoleByIdService defaultGetRoleByIdService;
    private DefaultListRolesService defaultListRolesService;
    private DefaultUpdateRoleService defaultUpdateRoleService;
    private DefaultDeleteRoleService defaultDeleteRoleService;
    private DefaultGetPermissionByIdService defaultGetPermissionByIdService;
    private DefaultListPermissionsService defaultListPermissionsService;
    private DefaultUpdatePermissionService defaultUpdatePermissionService;
    private DefaultDeletePermissionService defaultDeletePermissionService;

    @BeforeEach
    void setUp() {
        defaultCreateRoleService = new DefaultCreateRoleService(roleRepositoryPort);
        defaultCreatePermissionService = new DefaultCreatePermissionService(permissionRepositoryPort);
        defaultAssignRoleToUserService = new DefaultAssignRoleToUserService(
            userRepositoryPort,
            roleRepositoryPort
        );
        defaultAssignPermissionToRoleService = new DefaultAssignPermissionToRoleService(
            roleRepositoryPort,
            permissionRepositoryPort
        );
        defaultListRolesByUserService = new DefaultListRolesByUserService(userRepositoryPort);
        defaultGetRoleByIdService = new DefaultGetRoleByIdService(roleRepositoryPort);
        defaultListRolesService = new DefaultListRolesService(roleRepositoryPort);
        defaultUpdateRoleService = new DefaultUpdateRoleService(roleRepositoryPort);
        defaultDeleteRoleService = new DefaultDeleteRoleService(roleRepositoryPort);
        defaultGetPermissionByIdService = new DefaultGetPermissionByIdService(permissionRepositoryPort);
        defaultListPermissionsService = new DefaultListPermissionsService(permissionRepositoryPort);
        defaultUpdatePermissionService = new DefaultUpdatePermissionService(permissionRepositoryPort);
        defaultDeletePermissionService = new DefaultDeletePermissionService(permissionRepositoryPort);
    }

    @Test
    void createRoleShouldFailWhenNameAlreadyExists() {
        CreateRoleCommand command = new CreateRoleCommand("ADMIN", "Administrator");
        when(roleRepositoryPort.findByName("ADMIN")).thenReturn(Optional.of(new Role()));

        assertThatThrownBy(() -> defaultCreateRoleService.execute(command))
            .isInstanceOf(RoleAlreadyExistsException.class)
            .hasMessageContaining("ADMIN");
    }

    @Test
    void createPermissionShouldFailWhenCodeAlreadyExists() {
        CreatePermissionCommand command = new CreatePermissionCommand("PLAYLIST_READ", "Read playlist");
        when(permissionRepositoryPort.findByCode("PLAYLIST_READ")).thenReturn(Optional.of(new Permission()));

        assertThatThrownBy(() -> defaultCreatePermissionService.execute(command))
            .isInstanceOf(PermissionAlreadyExistsException.class)
            .hasMessageContaining("PLAYLIST_READ");
    }

    @Test
    void assignRoleToUserShouldFailWhenRoleDoesNotExist() {
        AssignRoleToUserCommand command = new AssignRoleToUserCommand("u-1", "r-1");

        when(userRepositoryPort.findById("u-1")).thenReturn(Optional.of(User.builder().id("u-1").build()));
        when(roleRepositoryPort.findById("r-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> defaultAssignRoleToUserService.execute(command))
            .isInstanceOf(RoleNotFoundException.class)
            .hasMessageContaining("r-1");
    }

    @Test
    void assignRoleToUserShouldCallRepositoryWhenDataExists() {
        AssignRoleToUserCommand command = new AssignRoleToUserCommand("u-1", "r-1");

        when(userRepositoryPort.findById("u-1")).thenReturn(Optional.of(User.builder().id("u-1").build()));
        when(roleRepositoryPort.findById("r-1")).thenReturn(Optional.of(Role.builder().id("r-1").build()));

        defaultAssignRoleToUserService.execute(command);

        verify(userRepositoryPort).addRoleToUser("u-1", "r-1");
    }

    @Test
    void assignPermissionToRoleShouldFailWhenPermissionDoesNotExist() {
        AssignPermissionToRoleCommand command = new AssignPermissionToRoleCommand("r-1", "p-1");

        when(roleRepositoryPort.findById("r-1")).thenReturn(Optional.of(Role.builder().id("r-1").build()));
        when(permissionRepositoryPort.findById("p-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> defaultAssignPermissionToRoleService.execute(command))
            .isInstanceOf(PermissionNotFoundException.class)
            .hasMessageContaining("p-1");
    }

    @Test
    void assignPermissionToRoleShouldCallRepositoryWhenDataExists() {
        AssignPermissionToRoleCommand command = new AssignPermissionToRoleCommand("r-1", "p-1");

        when(roleRepositoryPort.findById("r-1")).thenReturn(Optional.of(Role.builder().id("r-1").build()));
        when(permissionRepositoryPort.findById("p-1")).thenReturn(Optional.of(Permission.builder().id("p-1").build()));

        defaultAssignPermissionToRoleService.execute(command);

        verify(roleRepositoryPort).addPermissionToRole("r-1", "p-1");
    }

    @Test
    void listRolesByUserShouldFailWhenUserNotFound() {
        when(userRepositoryPort.findById("u-404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> defaultListRolesByUserService.execute("u-404"))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("u-404");
    }

    @Test
    void listRolesByUserShouldReturnRoles() {
        Role admin = Role.builder().id("r-1").name("ADMIN").description("Admin role").build();

        when(userRepositoryPort.findById("u-1")).thenReturn(Optional.of(User.builder().id("u-1").build()));
        when(userRepositoryPort.findRolesByUserId("u-1")).thenReturn(List.of(admin));

        List<Role> result = defaultListRolesByUserService.execute("u-1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("ADMIN");
        verify(userRepositoryPort).findRolesByUserId("u-1");
    }

    @Test
    void getRoleByIdShouldThrowWhenRoleNotFound() {
        when(roleRepositoryPort.findById("r-404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> defaultGetRoleByIdService.execute("r-404"))
            .isInstanceOf(RoleNotFoundException.class)
            .hasMessageContaining("r-404");
    }

    @Test
    void listRolesShouldReturnAllRoles() {
        when(roleRepositoryPort.findAll()).thenReturn(List.of(
            Role.builder().id("r-1").name("ADMIN").description("Admin").build(),
            Role.builder().id("r-2").name("USER").description("User").build()
        ));

        List<Role> result = defaultListRolesService.execute();

        assertThat(result).hasSize(2);
    }

    @Test
    void updateRoleShouldThrowWhenNameAlreadyUsedByAnotherRole() {
        UpdateRoleCommand command = new UpdateRoleCommand("r-1", "ADMIN", "Updated");

        when(roleRepositoryPort.findById("r-1")).thenReturn(
            Optional.of(Role.builder().id("r-1").name("OLD").description("Old").build())
        );
        when(roleRepositoryPort.findByName("ADMIN")).thenReturn(
            Optional.of(Role.builder().id("r-2").name("ADMIN").description("Admin").build())
        );

        assertThatThrownBy(() -> defaultUpdateRoleService.execute(command))
            .isInstanceOf(RoleAlreadyExistsException.class)
            .hasMessageContaining("ADMIN");
    }

    @Test
    void deleteRoleShouldCallRepositoryDelete() {
        when(roleRepositoryPort.findById("r-1")).thenReturn(
            Optional.of(Role.builder().id("r-1").name("ADMIN").description("Admin").build())
        );

        defaultDeleteRoleService.execute("r-1");

        verify(roleRepositoryPort).deleteById("r-1");
    }

    @Test
    void listPermissionsShouldReturnAllPermissions() {
        when(permissionRepositoryPort.findAll()).thenReturn(List.of(
            Permission.builder().id("p-1").code("USER_READ").description("Read user").build(),
            Permission.builder().id("p-2").code("USER_WRITE").description("Write user").build()
        ));

        List<Permission> result = defaultListPermissionsService.execute();

        assertThat(result).hasSize(2);
    }

    @Test
    void updatePermissionShouldThrowWhenCodeAlreadyUsedByAnotherPermission() {
        UpdatePermissionCommand command = new UpdatePermissionCommand("p-1", "USER_WRITE", "Updated");

        when(permissionRepositoryPort.findById("p-1")).thenReturn(
            Optional.of(Permission.builder().id("p-1").code("USER_READ").description("Read").build())
        );
        when(permissionRepositoryPort.findByCode("USER_WRITE")).thenReturn(
            Optional.of(Permission.builder().id("p-2").code("USER_WRITE").description("Write").build())
        );

        assertThatThrownBy(() -> defaultUpdatePermissionService.execute(command))
            .isInstanceOf(PermissionAlreadyExistsException.class)
            .hasMessageContaining("USER_WRITE");
    }

    @Test
    void deletePermissionShouldCallRepositoryDelete() {
        when(permissionRepositoryPort.findById("p-1")).thenReturn(
            Optional.of(Permission.builder().id("p-1").code("USER_READ").description("Read").build())
        );

        defaultDeletePermissionService.execute("p-1");

        verify(permissionRepositoryPort).deleteById("p-1");
    }
}
