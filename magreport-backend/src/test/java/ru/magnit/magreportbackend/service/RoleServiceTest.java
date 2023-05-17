package ru.magnit.magreportbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.magnit.magreportbackend.domain.folderreport.FolderAuthorityEnum;
import ru.magnit.magreportbackend.dto.request.folder.FolderSearchRequest;
import ru.magnit.magreportbackend.dto.request.folder.FolderTypes;
import ru.magnit.magreportbackend.dto.request.role.RolePermissionAddRequest;
import ru.magnit.magreportbackend.dto.request.role.RolePermissionDeleteRequest;
import ru.magnit.magreportbackend.dto.request.user.DomainGroupADRequest;
import ru.magnit.magreportbackend.dto.request.user.DomainGroupRequest;
import ru.magnit.magreportbackend.dto.request.user.RoleAddRequest;
import ru.magnit.magreportbackend.dto.request.user.RoleDomainGroupSetRequest;
import ru.magnit.magreportbackend.dto.request.user.RoleRequest;
import ru.magnit.magreportbackend.dto.request.user.RoleTypeRequest;
import ru.magnit.magreportbackend.dto.request.user.RoleUsersSetRequest;
import ru.magnit.magreportbackend.dto.response.folder.FolderSearchResponse;
import ru.magnit.magreportbackend.dto.response.role.RoleFolderResponse;
import ru.magnit.magreportbackend.dto.response.securityfilter.SecurityFilterShortResponse;
import ru.magnit.magreportbackend.dto.response.user.DomainGroupResponse;
import ru.magnit.magreportbackend.dto.response.user.RoleDomainGroupResponse;
import ru.magnit.magreportbackend.dto.response.user.RoleResponse;
import ru.magnit.magreportbackend.dto.response.user.RoleTypeResponse;
import ru.magnit.magreportbackend.dto.response.user.RoleUsersResponse;
import ru.magnit.magreportbackend.dto.response.user.UserResponse;
import ru.magnit.magreportbackend.exception.InvalidParametersException;
import ru.magnit.magreportbackend.service.domain.DataSetDomainService;
import ru.magnit.magreportbackend.service.domain.DataSourceDomainService;
import ru.magnit.magreportbackend.service.domain.DomainService;
import ru.magnit.magreportbackend.service.domain.ExcelTemplateDomainService;
import ru.magnit.magreportbackend.service.domain.FilterInstanceDomainService;
import ru.magnit.magreportbackend.service.domain.FilterTemplateDomainService;
import ru.magnit.magreportbackend.service.domain.FolderDomainService;
import ru.magnit.magreportbackend.service.domain.FolderPermissionsDomainService;
import ru.magnit.magreportbackend.service.domain.LdapService;
import ru.magnit.magreportbackend.service.domain.ReportDomainService;
import ru.magnit.magreportbackend.service.domain.RoleDomainService;
import ru.magnit.magreportbackend.service.domain.SecurityFilterDomainService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private static final Long FOLDER_ID = 1L;
    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final Long TYPE_ID = 5L;

    @Mock
    private RoleDomainService roleDomainService;

    @Mock
    private LdapService ldapService;
    @Mock
    private FilterTemplateDomainService filterTemplateDomainService;
    @Mock
    private ExcelTemplateDomainService excelTemplateDomainService;
    @Mock
    private FilterInstanceDomainService filterInstanceDomainService;
    @Mock
    private SecurityFilterDomainService securityFilterDomainService;
    @Mock
    private DataSourceDomainService dataSourceDomainService;
    @Mock
    private DataSetDomainService dataSetDomainService;
    @Mock
    private ReportDomainService reportDomainService;
    @Mock
    private FolderDomainService folderDomainService;
    @Mock
    private FolderPermissionsDomainService folderPermissionsDomainService;
    @Mock
    private DomainService domainService;


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

        when(roleDomainService.searchRole(any())).thenReturn(new FolderSearchResponse(Collections.emptyList(), Collections.emptyList()));
        assertNotNull(service.searchRole(new FolderSearchRequest()));

        verify(roleDomainService).searchRole(any());
        verifyNoMoreInteractions(roleDomainService);

    }

    @Test
    void deleteRoleExceptionTest1() {
        when(roleDomainService.getRoleDomainGroups(anyLong())).thenReturn(getRoleDomainGroupResponse());
        when(roleDomainService.getRoleUsers(anyLong())).thenReturn(getRoleUsersResponse());
        when(securityFilterDomainService.getFiltersWithSettingsForRole(any())).thenReturn(Collections.singletonList(getSecurityFilterShortResponse()));
        when(reportDomainService.getFoldersPermittedToRole(any())).thenReturn(Collections.singletonList(getRoleFolderResponse()));


        var request = getRoleRequest();
        assertThrows(InvalidParametersException.class, () -> service.deleteRole(request));

        verify(roleDomainService).getRoleDomainGroups(anyLong());
        verifyNoMoreInteractions(roleDomainService);
    }


    @Test
    void deleteRole() {
        when(roleDomainService.getRoleDomainGroups(anyLong())).thenReturn(getEmptyRoleDomainGroupResponse());
        when(roleDomainService.getRoleUsers(anyLong())).thenReturn(getRoleUsersResponse().setUsers(Collections.emptyList()));


        service.deleteRole(getRoleRequest());

        verify(roleDomainService).deleteRole(anyLong());
        verify(roleDomainService).getRoleDomainGroups(anyLong());
        verifyNoMoreInteractions(roleDomainService);
    }

    @Test
    void getADDomainGroupsTest1() {

        ReflectionTestUtils.setField(service, "defaultDomain", "TEST");

        assertEquals(0, service.getADDomainGroups(getDomainGroupADRequest(Collections.emptyList())).size());

        verify(ldapService).getGroupsByNamePart(any(), any());
        verify(domainService).getIdMap(any());
        verifyNoMoreInteractions(domainService, ldapService);
    }

    @Test
    void getADDomainGroupsTest2() {

        assertEquals(0, service.getADDomainGroups(getDomainGroupADRequest(Collections.singletonList("TEST"))).size());

        verify(ldapService).getGroupsByNamePart(any(), any());
        verify(domainService).getIdMap(any());
        verifyNoMoreInteractions(domainService, ldapService);
    }


    @Test
    void addFolderPermissionTest1() {

        service.addFolderPermission(getRolePermissionAddRequest(FolderTypes.PUBLISHED_REPORT));

        verify(folderPermissionsDomainService).getFolderReportBranch(anyLong());
        verify(folderDomainService).deleteFolderPermittedToRole(any(), any());
        verify(folderDomainService).addFolderPermittedToRole(any(), any(), any());

        verifyNoMoreInteractions(folderDomainService, folderPermissionsDomainService);
    }

    @Test
    void addFolderPermissionTest2() {

        service.addFolderPermission(getRolePermissionAddRequest(FolderTypes.REPORT_FOLDER));

        verify(folderPermissionsDomainService).getReportFolderBranch(anyLong());
        verify(reportDomainService).deleteFolderPermittedToRole(any(), any());
        verify(reportDomainService).addFolderPermittedToRole(any(), any(), any());

        verifyNoMoreInteractions(reportDomainService, folderPermissionsDomainService);
    }

    @Test
    void addFolderPermissionTest3() {

        service.addFolderPermission(getRolePermissionAddRequest(FolderTypes.DATASOURCE));

        verify(folderPermissionsDomainService).getDataSourceFolderBranch(anyLong());
        verify(dataSourceDomainService).deleteFolderPermittedToRole(any(), any());
        verify(dataSourceDomainService).addFolderPermittedToRole(any(), any(), any());

        verifyNoMoreInteractions(dataSourceDomainService, folderPermissionsDomainService);
    }

    @Test
    void addFolderPermissionTest4() {

        service.addFolderPermission(getRolePermissionAddRequest(FolderTypes.DATASET));

        verify(folderPermissionsDomainService).getDataSetFolderBranch(anyLong());
        verify(dataSetDomainService).deleteFolderPermittedToRole(any(), any());
        verify(dataSetDomainService).addFolderPermittedToRole(any(), any(), any());

        verifyNoMoreInteractions(dataSetDomainService, folderPermissionsDomainService);
    }

    @Test
    void addFolderPermissionTest5() {

        service.addFolderPermission(getRolePermissionAddRequest(FolderTypes.FILTER_INSTANCE));

        verify(folderPermissionsDomainService).getFilterInstanceFolderBranch(anyLong());
        verify(filterInstanceDomainService).deleteFolderPermittedToRole(any(), any());
        verify(filterInstanceDomainService).addFolderPermittedToRole(any(), any(), any());

        verifyNoMoreInteractions(filterInstanceDomainService, folderPermissionsDomainService);
    }

    @Test
    void addFolderPermissionTest6() {

        service.addFolderPermission(getRolePermissionAddRequest(FolderTypes.FILTER_TEMPLATE));

        verify(folderPermissionsDomainService).getFilterTemplateFolderBranch(anyLong());
        verify(filterTemplateDomainService).deleteFolderPermittedToRole(any(), any());
        verify(filterTemplateDomainService).addFolderPermittedToRole(any(), any(), any());

        verifyNoMoreInteractions(filterTemplateDomainService, folderPermissionsDomainService);
    }

    @Test
    void addFolderPermissionTest7() {

        service.addFolderPermission(getRolePermissionAddRequest(FolderTypes.SECURITY_FILTER));

        verify(folderPermissionsDomainService).getSecurityFilterFolderBranch(anyLong());
        verify(securityFilterDomainService).deleteFolderPermittedToRole(any(), any());
        verify(securityFilterDomainService).addFolderPermittedToRole(any(), any(), any());

        verifyNoMoreInteractions(securityFilterDomainService, folderPermissionsDomainService);
    }


    @Test
    void addFolderPermissionTest8() {

        service.addFolderPermission(getRolePermissionAddRequest(FolderTypes.EXCEL_TEMPLATE));

        verify(folderPermissionsDomainService).getExcelTemplateFolderBranch(anyLong());
        verify(excelTemplateDomainService).deleteFolderPermittedToRole(any(), any());
        verify(excelTemplateDomainService).addFolderPermittedToRole(any(), any(), any());

        verifyNoMoreInteractions(excelTemplateDomainService, folderPermissionsDomainService);
    }


    @Test
    void deleteFolderPermissionTest1() {

        service.deleteFolderPermission(getRolePermissionDeleteRequest(FolderTypes.PUBLISHED_REPORT));

        verify(folderPermissionsDomainService).getFolderReportBranch(anyLong());
        verify(folderDomainService).deleteFolderPermittedToRole(any(), any());
        verifyNoMoreInteractions(folderPermissionsDomainService, folderDomainService);

    }

    @Test
    void deleteFolderPermissionTest2() {

        service.deleteFolderPermission(getRolePermissionDeleteRequest(FolderTypes.REPORT_FOLDER));

        verify(folderPermissionsDomainService).getReportFolderBranch(anyLong());
        verify(reportDomainService).deleteFolderPermittedToRole(any(), any());
        verifyNoMoreInteractions(folderPermissionsDomainService, reportDomainService);

    }

    @Test
    void deleteFolderPermissionTest3() {

        service.deleteFolderPermission(getRolePermissionDeleteRequest(FolderTypes.DATASOURCE));

        verify(folderPermissionsDomainService).getDataSourceFolderBranch(anyLong());
        verify(dataSourceDomainService).deleteFolderPermittedToRole(any(), any());
        verifyNoMoreInteractions(folderPermissionsDomainService, dataSourceDomainService);

    }

    @Test
    void deleteFolderPermissionTest4() {

        service.deleteFolderPermission(getRolePermissionDeleteRequest(FolderTypes.DATASET));

        verify(folderPermissionsDomainService).getDataSetFolderBranch(anyLong());
        verify(dataSetDomainService).deleteFolderPermittedToRole(any(), any());
        verifyNoMoreInteractions(folderPermissionsDomainService, dataSetDomainService);

    }

    @Test
    void deleteFolderPermissionTest5() {

        service.deleteFolderPermission(getRolePermissionDeleteRequest(FolderTypes.EXCEL_TEMPLATE));

        verify(folderPermissionsDomainService).getExcelTemplateFolderBranch(anyLong());
        verify(excelTemplateDomainService).deleteFolderPermittedToRole(any(), any());
        verifyNoMoreInteractions(excelTemplateDomainService, folderPermissionsDomainService);

    }

    @Test
    void deleteFolderPermissionTest6() {

        service.deleteFolderPermission(getRolePermissionDeleteRequest(FolderTypes.FILTER_INSTANCE));

        verify(folderPermissionsDomainService).getFilterInstanceFolderBranch(anyLong());
        verify(filterInstanceDomainService).deleteFolderPermittedToRole(any(), any());
        verifyNoMoreInteractions(filterInstanceDomainService, folderPermissionsDomainService);

    }

    @Test
    void deleteFolderPermissionTest7() {

        service.deleteFolderPermission(getRolePermissionDeleteRequest(FolderTypes.FILTER_TEMPLATE));

        verify(folderPermissionsDomainService).getFilterTemplateFolderBranch(anyLong());
        verify(filterTemplateDomainService).deleteFolderPermittedToRole(any(), any());
        verifyNoMoreInteractions(filterTemplateDomainService, folderPermissionsDomainService);

    }

    @Test
    void deleteFolderPermissionTest8() {

        service.deleteFolderPermission(getRolePermissionDeleteRequest(FolderTypes.SECURITY_FILTER));

        verify(folderPermissionsDomainService).getSecurityFilterFolderBranch(anyLong());
        verify(securityFilterDomainService).deleteFolderPermittedToRole(any(), any());
        verifyNoMoreInteractions(securityFilterDomainService, folderPermissionsDomainService);


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

    private RoleDomainGroupSetRequest getRoleDomainGroupSetRequest() {
        return new RoleDomainGroupSetRequest()
                .setId(ID)
                .setDomainGroups(Collections.singletonList(new DomainGroupRequest(1L, "Group 1")));
    }

    private RoleDomainGroupResponse getRoleDomainGroupResponse() {
        return new RoleDomainGroupResponse(new RoleResponse(),
                Collections.singletonList(new DomainGroupResponse()
                        .setDomainId(1L)
                        .setDomainName("Test domain")
                        .setGroupName("Test group")));
    }

    private RoleDomainGroupResponse getEmptyRoleDomainGroupResponse() {
        return new RoleDomainGroupResponse(
                new RoleResponse(),
                Collections.emptyList());
    }

    private DomainGroupADRequest getDomainGroupADRequest(List<String> names) {
        return new DomainGroupADRequest()
                .setDomainNames(names)
                .setNamePart("")
                .setMaxResults(0L);
    }

    private RolePermissionAddRequest getRolePermissionAddRequest(FolderTypes type) {
        return new RolePermissionAddRequest()
                .setRoleId(ID)
                .setFolderId(FOLDER_ID)
                .setType(type)
                .setPermissions(Collections.emptyList());
    }

    private RolePermissionDeleteRequest getRolePermissionDeleteRequest(FolderTypes type) {
        return new RolePermissionDeleteRequest()
                .setRoleId(ID)
                .setFolderId(FOLDER_ID)
                .setType(type);
    }

    private SecurityFilterShortResponse getSecurityFilterShortResponse() {
        return new SecurityFilterShortResponse(
                ID,
                NAME,
                DESCRIPTION,
                "USER",
                LocalDateTime.now(),
                LocalDateTime.now(),
                Collections.emptyList()
        );
    }

    private RoleFolderResponse getRoleFolderResponse() {
        return new RoleFolderResponse(
                FOLDER_ID,
                "TEST",
                FolderAuthorityEnum.NONE,
                FolderTypes.REPORT_FOLDER
        );
    }
}
