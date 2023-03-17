package ru.magnit.magreportbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.magnit.magreportbackend.domain.folderreport.FolderAuthorityEnum;
import ru.magnit.magreportbackend.dto.inner.RoleView;
import ru.magnit.magreportbackend.dto.inner.UserView;
import ru.magnit.magreportbackend.dto.request.folder.FolderRequest;
import ru.magnit.magreportbackend.dto.request.folder.FolderTypes;
import ru.magnit.magreportbackend.dto.request.folder.PermissionCheckRequest;
import ru.magnit.magreportbackend.dto.request.folderreport.FolderPermissionSetRequest;
import ru.magnit.magreportbackend.dto.response.dataset.DataSetFolderResponse;
import ru.magnit.magreportbackend.dto.response.dataset.DataSetResponse;
import ru.magnit.magreportbackend.dto.response.datasource.DataSourceFolderResponse;
import ru.magnit.magreportbackend.dto.response.datasource.DataSourceResponse;
import ru.magnit.magreportbackend.dto.response.exceltemplate.ExcelTemplateFolderResponse;
import ru.magnit.magreportbackend.dto.response.exceltemplate.ExcelTemplateResponse;
import ru.magnit.magreportbackend.dto.response.filterinstance.FilterInstanceFolderResponse;
import ru.magnit.magreportbackend.dto.response.filterinstance.FilterInstanceResponse;
import ru.magnit.magreportbackend.dto.response.filtertemplate.FilterTemplateFolderResponse;
import ru.magnit.magreportbackend.dto.response.filtertemplate.FilterTemplateResponse;
import ru.magnit.magreportbackend.dto.response.folderreport.FolderResponse;
import ru.magnit.magreportbackend.dto.response.permission.DataSetFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.DataSourceFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.ExcelTemplateFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.FilterInstanceFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.FilterTemplateFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.FolderPermissionCheckResponse;
import ru.magnit.magreportbackend.dto.response.permission.FolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.ReportFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.SecurityFilterFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportFolderResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportResponse;
import ru.magnit.magreportbackend.dto.response.securityfilter.SecurityFilterFolderResponse;
import ru.magnit.magreportbackend.dto.response.user.DomainShortResponse;
import ru.magnit.magreportbackend.dto.response.user.RoleResponse;
import ru.magnit.magreportbackend.service.domain.DataSetDomainService;
import ru.magnit.magreportbackend.service.domain.DataSourceDomainService;
import ru.magnit.magreportbackend.service.domain.ExcelTemplateDomainService;
import ru.magnit.magreportbackend.service.domain.FilterInstanceDomainService;
import ru.magnit.magreportbackend.service.domain.FilterTemplateDomainService;
import ru.magnit.magreportbackend.service.domain.FolderDomainService;
import ru.magnit.magreportbackend.service.domain.FolderPermissionsDomainService;
import ru.magnit.magreportbackend.service.domain.ReportDomainService;
import ru.magnit.magreportbackend.service.domain.RoleDomainService;
import ru.magnit.magreportbackend.service.domain.SecurityFilterDomainService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FolderPermissionsServiceTest {

    private final Long ID = 1L;
    private final String NAME = "Test folder";
    private final String DESCRIPTION = "Folder description";
    private final LocalDateTime CREATED_TIME = LocalDateTime.now();
    private final LocalDateTime MODIFIED_TIME = LocalDateTime.now().plusMinutes(2);

    @InjectMocks
    private FolderPermissionsService service;

    @Mock
    private FolderPermissionsDomainService domainService;

    @Mock
    private UserDomainService userDomainService;
    @Mock
    private RoleDomainService roleDomainService;
    @Mock
    private  FolderDomainService folderDomainService;
    @Mock
    private  ReportDomainService reportDomainService;
    @Mock
    private  DataSourceDomainService dataSourceDomainService;
    @Mock
    private  DataSetDomainService dataSetDomainService;
    @Mock
    private  ExcelTemplateDomainService excelTemplateDomainService;
    @Mock
    private  FilterInstanceDomainService filterInstanceDomainService;
    @Mock
    private  FilterTemplateDomainService filterTemplateDomainService;
    @Mock
    private  SecurityFilterDomainService securityFilterDomainService;


    @Test
    void getFolderReportPermissions() {

        when(domainService.getFolderReportPermissions(any())).thenReturn(getFolderPermissionsResponse());

        FolderPermissionsResponse response = service.getFolderReportPermissions(new FolderRequest().setId(ID));

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());

        verify(domainService).getFolderReportPermissions(any());
        verifyNoMoreInteractions(domainService);

    }

    @Test
    void setFolderReportPermissions() {
        when(domainService.getFolderReportBranch(any())).thenReturn(new ArrayList<>());
        when(domainService.getFolderReportPermissions(any())).thenReturn(getFolderPermissionsResponse());
        when(folderDomainService.getPathToFolder(any())).thenReturn(Collections.emptyList());

        FolderPermissionsResponse response = service.setFolderReportPermissions(getFolderPermissionsRequest());

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());

        verifyNoMoreInteractions(domainService);
    }

    @Test
    void getReportFolderPermissions() {
        when(domainService.getReportFolderPermissions(any())).thenReturn(new ReportFolderPermissionsResponse(getReportFolderResponse(), Collections.emptyList()));

        ReportFolderPermissionsResponse response = service.getReportFolderPermissions(new FolderRequest().setId(ID));

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());

        verify(domainService).getReportFolderPermissions(any());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void setReportFolderPermissions() {
        when(domainService.getReportFolderBranch(any())).thenReturn(new ArrayList<>());
        when(domainService.getReportFolderPermissions(any())).thenReturn(new ReportFolderPermissionsResponse(getReportFolderResponse(), Collections.emptyList()));
        when(reportDomainService.getPathToFolder(any())).thenReturn(Collections.emptyList());

        ReportFolderPermissionsResponse response = service.setReportFolderPermissions(getFolderPermissionsRequest());

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());


        verifyNoMoreInteractions(domainService);
    }

    @Test
    void getDataSourceFolderPermissions() {
        when(domainService.getDataSourceFolderPermissions(any())).thenReturn(new DataSourceFolderPermissionsResponse(getDataSourceFolderResponse(), Collections.emptyList()));

        DataSourceFolderPermissionsResponse response = service.getDataSourceFolderPermissions(new FolderRequest().setId(ID));

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());

        verify(domainService).getDataSourceFolderPermissions(any());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void setDataSourceFolderPermissions() {
        when(domainService.getDataSourceFolderBranch(any())).thenReturn(new ArrayList<>());
        when(dataSourceDomainService.getPathToFolder(any())).thenReturn(Collections.emptyList());
        when(domainService.getDataSourceFolderPermissions(any())).thenReturn(new DataSourceFolderPermissionsResponse(getDataSourceFolderResponse(), Collections.emptyList()));

        DataSourceFolderPermissionsResponse response = service.setDataSourceFolderPermissions(getFolderPermissionsRequest());

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());


        verifyNoMoreInteractions(domainService);
    }

    @Test
    void getDataSetFolderPermissions() {
        when(domainService.getDataSetFolderPermissions(any())).thenReturn(new DataSetFolderPermissionsResponse(getDataSetFolderResponse(), Collections.emptyList()));

        DataSetFolderPermissionsResponse response = service.getDataSetFolderPermissions(new FolderRequest().setId(ID));

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());

        verify(domainService).getDataSetFolderPermissions(any());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void setDataSetFolderPermissions() {
        when(domainService.getDataSetFolderBranch(any())).thenReturn(new ArrayList<>());
        when(domainService.getDataSetFolderPermissions(any())).thenReturn(new DataSetFolderPermissionsResponse(getDataSetFolderResponse(), Collections.emptyList()));
        when(dataSetDomainService.getPathToFolder(any())).thenReturn(Collections.emptyList());

        DataSetFolderPermissionsResponse response = service.setDataSetFolderPermissions(getFolderPermissionsRequest());

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());

        verifyNoMoreInteractions(domainService);
    }

    @Test
    void getExcelTemplateFolderPermissions() {
        when(domainService.getExcelTemplateFolderPermissions(any())).thenReturn(new ExcelTemplateFolderPermissionsResponse(getExcelTemplateFolderResponse(), Collections.emptyList()));

        ExcelTemplateFolderPermissionsResponse response = service.getExcelTemplateFolderPermissions(new FolderRequest().setId(ID));

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());

        verify(domainService).getExcelTemplateFolderPermissions(any());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void setExcelTemplateFolderPermissions() {
        when(domainService.getExcelTemplateFolderBranch(any())).thenReturn(new ArrayList<>());
        when(domainService.getExcelTemplateFolderPermissions(any())).thenReturn(new ExcelTemplateFolderPermissionsResponse(getExcelTemplateFolderResponse(), Collections.emptyList()));
        when(excelTemplateDomainService.getPathToFolder(any())).thenReturn(Collections.emptyList());

        ExcelTemplateFolderPermissionsResponse response = service.setExcelTemplateFolderPermissions(getFolderPermissionsRequest());

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());

        verifyNoMoreInteractions(domainService);
    }

    @Test
    void getFilterInstanceFolderPermissions() {
        when(domainService.getFilterInstanceFolderPermissions(any())).thenReturn(new FilterInstanceFolderPermissionsResponse(getFilterInstanceFolderResponse(), Collections.emptyList()));

        FilterInstanceFolderPermissionsResponse response = service.getFilterInstanceFolderPermissions(new FolderRequest().setId(ID));

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());

        verify(domainService).getFilterInstanceFolderPermissions(any());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void setFilterInstanceFolderPermissions() {
        when(domainService.getFilterInstanceFolderBranch(any())).thenReturn(new ArrayList<>());
        when(domainService.getFilterInstanceFolderPermissions(any())).thenReturn(new FilterInstanceFolderPermissionsResponse(getFilterInstanceFolderResponse(), Collections.emptyList()));
        when(filterInstanceDomainService.getPathToFolder(any())).thenReturn(Collections.emptyList());

        FilterInstanceFolderPermissionsResponse response = service.setFilterInstanceFolderPermissions(getFolderPermissionsRequest());

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());

        verifyNoMoreInteractions(domainService);
    }

    @Test
    void getFilterTemplateFolderPermissions() {
        when(domainService.getFilterTemplateFolderPermissions(any())).thenReturn(new FilterTemplateFolderPermissionsResponse(getFilterTemplateFolderResponse(), Collections.emptyList()));

        FilterTemplateFolderPermissionsResponse response = service.getFilterTemplateFolderPermissions(new FolderRequest().setId(ID));

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());

        verify(domainService).getFilterTemplateFolderPermissions(any());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void setFilterTemplateFolderPermissions() {
        when(domainService.getFilterTemplateFolderBranch(any())).thenReturn(new ArrayList<>());
        when(domainService.getFilterTemplateFolderPermissions(any())).thenReturn(new FilterTemplateFolderPermissionsResponse(getFilterTemplateFolderResponse(), Collections.emptyList()));
        when(filterTemplateDomainService.getPathToFolder(any())).thenReturn(Collections.emptyList());

        FilterTemplateFolderPermissionsResponse response = service.setFilterTemplateFolderPermissions(getFolderPermissionsRequest());

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());

        verifyNoMoreInteractions(domainService);
    }

    @Test
    void getSecurityFilterFolderPermissions() {
        when(domainService.getSecurityFilterFolderPermissions(any())).thenReturn(new SecurityFilterFolderPermissionsResponse(getSecurityFilterFolderResponse(), Collections.emptyList()));

        SecurityFilterFolderPermissionsResponse response = service.getSecurityFilterFolderPermissions(new FolderRequest().setId(ID));

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());

        verify(domainService).getSecurityFilterFolderPermissions(any());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void setSecurityFilterFolderPermissions() {
        when(domainService.getSecurityFilterFolderBranch(any())).thenReturn(new ArrayList<>());
        when(domainService.getSecurityFilterFolderPermissions(any())).thenReturn(new SecurityFilterFolderPermissionsResponse(getSecurityFilterFolderResponse(), Collections.emptyList()));
        when(securityFilterDomainService.getPathToFolder(any())).thenReturn(Collections.emptyList());

        SecurityFilterFolderPermissionsResponse response = service.setSecurityFilterFolderPermissions(getFolderPermissionsRequest());

        assertNotNull(response.folder());
        assertNotNull(response.rolePermissions());

        verifyNoMoreInteractions(domainService);
    }

    @Test
    void checkFolderReportPermission() {

        when(domainService.checkFolderPermission(any())).thenReturn(new FolderPermissionCheckResponse().setAuthority(FolderAuthorityEnum.WRITE));

        var response = service.checkFolderPermission(getPermissionCheckRequest(FolderTypes.PUBLISHED_REPORT));
        assertEquals(FolderAuthorityEnum.WRITE, response.getAuthority());

        verify(domainService).checkFolderPermission(any());
        verifyNoMoreInteractions(domainService, userDomainService);
    }




    private FolderResponse getFolderResponse() {
        return new FolderResponse()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setChildFolders(new LinkedList<>(Collections.singletonList(new FolderResponse())))
                .setReports(Collections.singletonList(new ReportResponse()))
                .setCreated(CREATED_TIME)
                .setModified(MODIFIED_TIME);
    }

    private ReportFolderResponse getReportFolderResponse() {
        return new ReportFolderResponse()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setChildFolders(new LinkedList<>(Collections.singletonList(new ReportFolderResponse())))
                .setReports(Collections.singletonList(new ReportResponse()))
                .setCreated(CREATED_TIME)
                .setModified(MODIFIED_TIME);
    }

    private DataSourceFolderResponse getDataSourceFolderResponse() {
        return new DataSourceFolderResponse()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setChildFolders(new LinkedList<>(Collections.singletonList(new DataSourceFolderResponse())))
                .setDataSources(Collections.singletonList(new DataSourceResponse()))
                .setCreated(CREATED_TIME)
                .setModified(MODIFIED_TIME);
    }

    private DataSetFolderResponse getDataSetFolderResponse() {
        return new DataSetFolderResponse()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setChildFolders(new LinkedList<>(Collections.singletonList(new DataSetFolderResponse())))
                .setDataSets(Collections.singletonList(new DataSetResponse()))
                .setCreated(CREATED_TIME)
                .setModified(MODIFIED_TIME);
    }

    private FilterInstanceFolderResponse getFilterInstanceFolderResponse() {
        return new FilterInstanceFolderResponse()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setChildFolders(new LinkedList<>(Collections.singletonList(new FilterInstanceFolderResponse())))
                .setFilterInstances(Collections.singletonList(new FilterInstanceResponse()))
                .setCreated(CREATED_TIME)
                .setModified(MODIFIED_TIME);
    }

    private FilterTemplateFolderResponse getFilterTemplateFolderResponse() {
        return new FilterTemplateFolderResponse()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setChildFolders(new LinkedList<>(Collections.singletonList(new FilterTemplateFolderResponse())))
                .setFilterTemplates(Collections.singletonList(new FilterTemplateResponse()))
                .setCreated(CREATED_TIME)
                .setModified(MODIFIED_TIME);
    }

    private SecurityFilterFolderResponse getSecurityFilterFolderResponse() {
        return new SecurityFilterFolderResponse()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setChildFolders(new LinkedList<>(Collections.singletonList(new SecurityFilterFolderResponse())))
                .setSecurityFilters(Collections.emptyList())
                .setCreated(CREATED_TIME)
                .setModified(MODIFIED_TIME);
    }

    private ExcelTemplateFolderResponse getExcelTemplateFolderResponse() {
        return new ExcelTemplateFolderResponse()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setChildFolders(new LinkedList<>(Collections.singletonList(new ExcelTemplateFolderResponse())))
                .setExcelTemplates(Collections.singletonList(new ExcelTemplateResponse()))
                .setCreated(CREATED_TIME)
                .setModified(MODIFIED_TIME);

    }

    private FolderPermissionsResponse getFolderPermissionsResponse() {
        return new FolderPermissionsResponse(getFolderResponse(), Collections.emptyList());
    }

    private FolderPermissionSetRequest getFolderPermissionsRequest() {
        return new FolderPermissionSetRequest(ID, Collections.emptyList(), true, true);
    }

    private PermissionCheckRequest getPermissionCheckRequest(FolderTypes type) {
        return new PermissionCheckRequest()
                .setId(ID)
                .setFolderType(type);
    }

    private UserView getCurrentUser(){
        return new UserView()
                .setName("TestUser")
                .setDomain(new DomainShortResponse(ID,NAME));
    }
}
