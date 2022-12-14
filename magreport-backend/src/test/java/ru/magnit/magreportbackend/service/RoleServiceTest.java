package ru.magnit.magreportbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.magnit.magreportbackend.dto.request.folder.FolderSearchRequest;
import ru.magnit.magreportbackend.dto.request.user.DomainGroupRequest;
import ru.magnit.magreportbackend.dto.request.user.RoleAddRequest;
import ru.magnit.magreportbackend.dto.request.user.RoleDomainGroupSetRequest;
import ru.magnit.magreportbackend.dto.request.user.RoleRequest;
import ru.magnit.magreportbackend.dto.request.user.RoleTypeRequest;
import ru.magnit.magreportbackend.dto.request.user.RoleUsersSetRequest;
import ru.magnit.magreportbackend.dto.response.folder.FolderSearchResponse;
import ru.magnit.magreportbackend.dto.response.user.DomainGroupResponse;
import ru.magnit.magreportbackend.dto.response.user.RoleDomainGroupResponse;
import ru.magnit.magreportbackend.dto.response.user.RoleResponse;
import ru.magnit.magreportbackend.dto.response.user.RoleTypeResponse;
import ru.magnit.magreportbackend.dto.response.user.RoleUsersResponse;
import ru.magnit.magreportbackend.dto.response.user.UserResponse;
import ru.magnit.magreportbackend.service.domain.LdapService;
import ru.magnit.magreportbackend.service.domain.RoleDomainService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    private static final Long ID = 1L;
    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final Long TYPE_ID = 5L;

    @Mock
    private RoleDomainService roleDomainService;

    @Mock
    private LdapService ldapService;

    @InjectMocks
    private RoleService service;

    @Test
    void addRole() {
        RoleAddRequest request = spy(getRoleAddRequest());

        when(roleDomainService.saveRole(request)).thenReturn(ID);
        when(roleDomainService.getRole(anyLong())).thenReturn(getRoleResponse());

        var result = service.addRole(request);

        assertEquals(ID, result.getId());
        verify(roleDomainService).saveRole(request);
        verify(roleDomainService).getRole(anyLong());
        verifyNoMoreInteractions(request, roleDomainService, ldapService);


        Mockito.reset(roleDomainService, ldapService);

        when(roleDomainService.saveRole(request)).thenReturn(null);

        result = service.addRole(request);

        assertNull(result);
        verify(roleDomainService).saveRole(request);
        verifyNoMoreInteractions(roleDomainService, ldapService);

    }

    @Test
    void getRoleType() {

        RoleTypeRequest request = spy(getRoleTypeRequest());

        when(roleDomainService.getRoleType(anyLong())).thenReturn(getRoleTypeResponse());

        final var result = service.getRoleType(request);
        assertEquals(ID, result.getId());

        verify(roleDomainService).getRoleType(anyLong());
        verify(request).getId();

        verifyNoMoreInteractions(request, roleDomainService, ldapService);
    }

    @Test
    void getRole() {

        RoleRequest request = spy(getRoleRequest());

        when(roleDomainService.getRole(anyLong())).thenReturn(getRoleResponse());

        final var result = service.getRole(request);
        assertEquals(ID, result.getId());

        verify(roleDomainService).getRole(anyLong());
        verify(request).getId();

        verifyNoMoreInteractions(request, roleDomainService, ldapService);
    }

    @Test
    void getRoleUsers() {

        RoleRequest request = spy(getRoleRequest());

        when(roleDomainService.getRoleUsers(anyLong())).thenReturn(getRoleUsersResponse());

        final var result = service.getRoleUsers(request);
        assertEquals(ID, result.getId());
        assertNotNull(result.getUsers());
        assertEquals(1, result.getUsers().size());

        verify(roleDomainService).getRoleUsers(anyLong());
        verify(request).getId();

        verifyNoMoreInteractions(request, roleDomainService, ldapService);
    }

    @Test
    void setRoleUsers() {

        RoleUsersSetRequest request = spy(getRoleUsersSetRequest());


        service.setRoleUsers(request);

        verify(roleDomainService).clearUsersFromRole(anyLong());
        verify(roleDomainService).setRoleUsers(request);
        verify(request).getId();

        verifyNoMoreInteractions(request, roleDomainService, ldapService);
    }

    @Test
    void editRole() {

        RoleAddRequest request = spy(getRoleAddRequest());
        RoleAddRequest nullRequest = spy(getRoleAddRequest());
        nullRequest.setId(null);
        Mockito.reset(nullRequest);

        //exists
        when(roleDomainService.updateRole(request)).thenReturn(ID);
        when(roleDomainService.getRole(anyLong())).thenReturn(getRoleResponse());

        var result = service.editRole(request);
        assertEquals(ID, result.getId());

        verify(roleDomainService).updateRole(request);
        verify(roleDomainService).getRole(anyLong());

        verifyNoMoreInteractions(request, roleDomainService, ldapService);
    }

    @Test
    void addRoleUsers() {

        RoleUsersSetRequest request = spy(getRoleUsersSetRequest());

        when(roleDomainService.getRoleUsers(anyLong())).thenReturn(getRoleUsersResponse());

        final var result = service.addRoleUsers(request);
        assertEquals(ID, result.getId());

        verify(roleDomainService).addUserToRole(anyLong(), anyList());
        verify(roleDomainService).getRoleUsers(anyLong());
        verify(request, times(2)).getId();
        verify(request).getUsers();

        verifyNoMoreInteractions(request, roleDomainService, ldapService);
    }

    @Test
    void deleteRoleUsers() {

        RoleUsersSetRequest request = spy(getRoleUsersSetRequest());

        when(roleDomainService.getRoleUsers(anyLong())).thenReturn(getRoleUsersResponse());

        service.deleteRoleUsers(request);

        verify(roleDomainService).clearUsersFromRole(anyLong(), anyList());
        verify(roleDomainService).getRoleUsers(anyLong());
        verify(request, times(2)).getId();
        verify(request).getUsers();

        verifyNoMoreInteractions(request, roleDomainService, ldapService);
    }

    @Test
    void addRoleDomainGroups() {

        RoleDomainGroupSetRequest request = spy(getRoleDomainGroupSetRequest());

        when(roleDomainService.getRoleDomainGroups(anyLong())).thenReturn(getRoleDomainGroupResponse());

        final var result = service.addRoleDomainGroups(request);

        assertNotNull(result.role());
        assertEquals(1, result.domainGroups().size());

        verify(roleDomainService).addDomainGroups(anyList());
        verify(roleDomainService).addRoleDomainGroups(request);
        verify(roleDomainService).getRoleDomainGroups(anyLong());
        verify(request).getId();
        verify(request).getDomainGroups();

        verifyNoMoreInteractions(request, roleDomainService, ldapService);
    }

    @Test
    void deleteRoleDomainGroups() {

        RoleDomainGroupSetRequest request = spy(getRoleDomainGroupSetRequest());

        when(roleDomainService.getRoleDomainGroups(anyLong())).thenReturn(getRoleDomainGroupResponse());

        final var result = service.deleteRoleDomainGroups(request);

        assertNotNull(result.role());
        assertEquals(1, result.domainGroups().size());


        verify(roleDomainService).deleteRoleDomainGroups(request);
        verify(roleDomainService).getRoleDomainGroups(anyLong());
        verify(request).getId();

        verifyNoMoreInteractions(request, roleDomainService, ldapService);
    }

    private RoleDomainGroupSetRequest getRoleDomainGroupSetRequest() {
        return new RoleDomainGroupSetRequest()
                .setId(ID)
                .setDomainGroups(Collections.singletonList(new DomainGroupRequest(1L, "Group 1")));
    }

    @Test
    void getRoleDomainGroups() {

        RoleRequest request = spy(getRoleRequest());

        when(roleDomainService.getRoleDomainGroups(anyLong())).thenReturn(getRoleDomainGroupResponse());

        final var result = service.getRoleDomainGroups(request);

        assertNotNull(result.role());
        assertEquals(1, result.domainGroups().size());

        verify(roleDomainService).getRoleDomainGroups(anyLong());
        verify(request).getId();

        verifyNoMoreInteractions(request, roleDomainService, ldapService);
    }

    private RoleDomainGroupResponse getRoleDomainGroupResponse() {
        return new RoleDomainGroupResponse(new RoleResponse(),
                Collections.singletonList(new DomainGroupResponse()
                    .setDomainId(1L)
                    .setDomainName("Test domain")
                    .setGroupName("Test group")));
    }

    @Test
    void getAllRoles() {

        when(roleDomainService.getAllRoles()).thenReturn(Collections.singletonList(getRoleResponse()));

        final var result = service.getAllRoles();
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(roleDomainService).getAllRoles();

        verifyNoMoreInteractions(roleDomainService, ldapService);
    }

    @Test
    void getAllRoleTypes() {

        when(roleDomainService.getAllRoleTypes()).thenReturn(Collections.singletonList(getRoleTypeResponse()));

        final var result = service.getAllRoleTypes();
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(roleDomainService).getAllRoleTypes();

        verifyNoMoreInteractions(roleDomainService, ldapService);
    }

    @Test
    void deleteRoleByName() {

        String roleName = "Name";

        service.deleteRole(roleName);

        verify(roleDomainService).deleteRole(roleName);

        verifyNoMoreInteractions(roleDomainService, ldapService);
    }

    @Test
    void getRoleByName() {

        String roleName = "Name";

        when(roleDomainService.getRoleByName(roleName)).thenReturn(getRoleResponse());

        service.getRoleByName(roleName);

        verify(roleDomainService).getRoleByName(roleName);

        verifyNoMoreInteractions(roleDomainService, ldapService);
    }

    @Test
    void searchRole() {

        when(roleDomainService.searchRole(any())).thenReturn(new FolderSearchResponse(Collections.emptyList(),Collections.emptyList()));
        assertNotNull(service.searchRole(new FolderSearchRequest()));

        verify(roleDomainService).searchRole(any());
        verifyNoMoreInteractions(roleDomainService);

    }

    private RoleAddRequest getRoleAddRequest() {
        return new RoleAddRequest()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setTypeId(TYPE_ID);
    }

    private RoleResponse getRoleResponse() {
        return new RoleResponse()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setTypeId(TYPE_ID);
    }

    private RoleTypeRequest getRoleTypeRequest() {
        return new RoleTypeRequest()
                .setId(ID);
    }

    private RoleTypeResponse getRoleTypeResponse() {
        return new RoleTypeResponse()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION);
    }

    private RoleRequest getRoleRequest() {
        return new RoleRequest()
                .setId(ID);
    }

    private RoleUsersResponse getRoleUsersResponse() {
        return new RoleUsersResponse()
                .setId(ID)
                .setUsers(Collections.singletonList(new UserResponse()));
    }

    private RoleUsersSetRequest getRoleUsersSetRequest() {
        return new RoleUsersSetRequest()
                .setId(ID)
                .setUsers(Collections.singletonList(1L));
    }
}
