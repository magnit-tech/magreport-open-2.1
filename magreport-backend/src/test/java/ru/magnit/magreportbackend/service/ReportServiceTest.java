package ru.magnit.magreportbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterFieldTypeEnum;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTypeEnum;
import ru.magnit.magreportbackend.domain.folderreport.FolderAuthorityEnum;
import ru.magnit.magreportbackend.dto.inner.RoleView;
import ru.magnit.magreportbackend.dto.inner.UserView;
import ru.magnit.magreportbackend.dto.inner.filter.FilterData;
import ru.magnit.magreportbackend.dto.inner.filter.FilterFieldData;
import ru.magnit.magreportbackend.dto.request.ChangeParentFolderRequest;
import ru.magnit.magreportbackend.dto.request.filterreport.FilterGroupAddRequest;
import ru.magnit.magreportbackend.dto.request.folder.CopyFolderRequest;
import ru.magnit.magreportbackend.dto.request.folder.FolderAddRequest;
import ru.magnit.magreportbackend.dto.request.folder.FolderChangeParentRequest;
import ru.magnit.magreportbackend.dto.request.folder.FolderRenameRequest;
import ru.magnit.magreportbackend.dto.request.folder.FolderRequest;
import ru.magnit.magreportbackend.dto.request.folder.FolderSearchRequest;
import ru.magnit.magreportbackend.dto.request.report.ReportAddFavoritesRequest;
import ru.magnit.magreportbackend.dto.request.report.ReportAddRequest;
import ru.magnit.magreportbackend.dto.request.report.ReportEditRequest;
import ru.magnit.magreportbackend.dto.request.report.ReportEncryptRequest;
import ru.magnit.magreportbackend.dto.request.report.ReportIdRequest;
import ru.magnit.magreportbackend.dto.request.report.ReportRequest;
import ru.magnit.magreportbackend.dto.request.report.ScheduleReportRequest;
import ru.magnit.magreportbackend.dto.response.dataset.DataSetFieldResponse;
import ru.magnit.magreportbackend.dto.response.dataset.DataSetResponse;
import ru.magnit.magreportbackend.dto.response.folder.FolderRoleResponse;
import ru.magnit.magreportbackend.dto.response.folder.FolderSearchResponse;
import ru.magnit.magreportbackend.dto.response.folderreport.FolderResponse;
import ru.magnit.magreportbackend.dto.response.permission.ReportFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.RolePermissionResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportFieldResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportFolderResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportJobFilterResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportResponse;
import ru.magnit.magreportbackend.dto.response.user.DomainShortResponse;
import ru.magnit.magreportbackend.dto.response.user.RoleResponse;
import ru.magnit.magreportbackend.exception.InvalidParametersException;
import ru.magnit.magreportbackend.mapper.report.ReportResponseMapper;
import ru.magnit.magreportbackend.repository.ReportFolderRepository;
import ru.magnit.magreportbackend.service.domain.DataSetDomainService;
import ru.magnit.magreportbackend.service.domain.ExcelTemplateDomainService;
import ru.magnit.magreportbackend.service.domain.FilterReportDomainService;
import ru.magnit.magreportbackend.service.domain.FolderEntitySearchDomainService;
import ru.magnit.magreportbackend.service.domain.FolderPermissionsDomainService;
import ru.magnit.magreportbackend.service.domain.JobDomainService;
import ru.magnit.magreportbackend.service.domain.OlapConfigurationDomainService;
import ru.magnit.magreportbackend.service.domain.OlapUserChoiceDomainService;
import ru.magnit.magreportbackend.service.domain.ReportDomainService;
import ru.magnit.magreportbackend.service.domain.ScheduleTaskDomainService;
import ru.magnit.magreportbackend.service.domain.SecurityFilterDomainService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;
import ru.magnit.magreportbackend.service.jobengine.filter.FilterQueryExecutor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    private final Long ID = 1L;
    private final String NAME = "Test folder";
    private final String DESCRIPTION = "Folder description";
    private final LocalDateTime CREATED_TIME = LocalDateTime.now();
    private final LocalDateTime MODIFIED_TIME = LocalDateTime.now().plusMinutes(2);

    @InjectMocks
    private ReportService service;

    @Mock
    private ReportDomainService domainService;

    @Mock
    private DataSetDomainService dataSetDomainService;

    @Mock
    private UserDomainService userDomainService;

    @Mock
    private FolderPermissionsDomainService folderPermissionsDomainService;

    @Mock
    private JobDomainService jobDomainService;

    @Mock
    private FilterReportDomainService filterReportDomainService;

    @Mock
    private FilterReportService filterReportService;

    @Mock
    private FolderEntitySearchDomainService folderEntitySearchDomainService;

    @Mock
    private ReportFolderRepository repository;

    @Mock
    private ReportResponseMapper mapper;

    @Mock
    private FilterQueryExecutor filterQueryExecutor;

    @Mock
    private ExcelTemplateDomainService excelTemplateDomainService;

    @Mock
    private ScheduleTaskDomainService scheduleTaskDomainService;

    @Mock
    private OlapConfigurationDomainService olapConfigurationDomainService;

    @Mock
    private OlapUserChoiceDomainService olapUserChoiceDomainService;

    @Mock
    private SecurityFilterDomainService securityFilterDomainService;

    @Mock
    private PermissionCheckerSystem permissionCheckerSystem;


    @Test
    void getFolder() {
        when(domainService.getFolder(any(), any())).thenReturn(getFolderResponse());
        when(userDomainService.getUserRoles(anyString(), anyString(), any())).thenReturn(Collections.singletonList(new RoleView().setId(0L)));
        when(userDomainService.getCurrentUser()).thenReturn(getCurrentUser());
        when(folderPermissionsDomainService.getReportFolderPermissionsForRoles(anyList(), anyList())).thenReturn(Collections.emptyList());

        ReportFolderResponse response = service.getFolder(new FolderRequest(ID));

        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(DESCRIPTION, response.getDescription());
        assertNotNull(response.getChildFolders());
        assertNotNull(response.getReports());
        assertEquals(CREATED_TIME, response.getCreated());
        assertEquals(MODIFIED_TIME, response.getModified());

        Mockito.reset(userDomainService);

        when(userDomainService.getUserRoles(anyString(), anyString(), any())).thenReturn(Collections.emptyList());
        when(userDomainService.getCurrentUser()).thenReturn(getCurrentUser());

        response = service.getFolder(new FolderRequest(ID));

        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(DESCRIPTION, response.getDescription());
        assertNotNull(response.getChildFolders());
        assertNotNull(response.getReports());
        assertEquals(CREATED_TIME, response.getCreated());
        assertEquals(MODIFIED_TIME, response.getModified());

        Mockito.reset(domainService);
        when(domainService.getFolder(any(), any())).thenReturn(getFolderResponse().setParentId(ID).setAuthority(FolderAuthorityEnum.NONE));

        response = service.getFolder(new FolderRequest(ID));

        assertNull(response);

        verify(domainService).getFolder(any(), any());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void addFolder() {
        when(domainService.addFolder(any())).thenReturn(getFolderResponse());
        when(folderPermissionsDomainService.getReportFolderPermissions(any())).thenReturn(new ReportFolderPermissionsResponse(new ReportFolderResponse().setId(ID), Collections.singletonList(new RolePermissionResponse(new RoleResponse(), Collections.singletonList(FolderAuthorityEnum.WRITE)))));
        when(domainService.getFolder(any(), any())).thenReturn(getFolderResponse());
        when(userDomainService.getCurrentUser()).thenReturn(new UserView().setId(1L));

        ReportFolderResponse response = service.addFolder(getFolderAddRequest());

        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(DESCRIPTION, response.getDescription());
        assertNotNull(response.getChildFolders());
        assertNotNull(response.getReports());
        assertEquals(CREATED_TIME, response.getCreated());
        assertEquals(MODIFIED_TIME, response.getModified());

        response = service.addFolder(getFolderAddRequest().setParentId(null));

        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(DESCRIPTION, response.getDescription());
        assertNotNull(response.getChildFolders());
        assertNotNull(response.getReports());
        assertEquals(CREATED_TIME, response.getCreated());
        assertEquals(MODIFIED_TIME, response.getModified());

        verify(domainService, times(2)).addFolder(any());
        verify(userDomainService, times(2)).getCurrentUser();
        verifyNoMoreInteractions(domainService);
        verifyNoMoreInteractions(userDomainService);
    }

    @Test
    void getChildFolders() {
        when(domainService.getChildFolders(any())).thenReturn(Collections.singletonList(getFolderResponse()));

        List<ReportFolderResponse> responses = service.getChildFolders(new FolderRequest().setId(ID));
        assertNotNull(responses);

        ReportFolderResponse response = responses.get(0);

        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(DESCRIPTION, response.getDescription());
        assertNotNull(response.getChildFolders());
        assertNotNull(response.getReports());
        assertEquals(CREATED_TIME, response.getCreated());
        assertEquals(MODIFIED_TIME, response.getModified());

        verify(domainService).getChildFolders(any());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void renameFolder() {
        when(domainService.renameFolder(any())).thenReturn(getFolderResponse());

        ReportFolderResponse response = service.renameFolder(getFolderRenameRequest());

        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(DESCRIPTION, response.getDescription());
        assertNotNull(response.getChildFolders());
        assertNotNull(response.getReports());
        assertEquals(CREATED_TIME, response.getCreated());
        assertEquals(MODIFIED_TIME, response.getModified());

        verify(domainService).renameFolder(any());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void deleteFolder() {
        service.deleteFolder(new FolderRequest().setId(ID));

        verify(domainService).deleteFolder(anyLong());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void addReport() {
        when(dataSetDomainService.getDataSet(anyLong())).thenReturn(new DataSetResponse().setIsValid(true));
        when(userDomainService.getCurrentUser()).thenReturn(new UserView().setId(1L));
        when(userDomainService.getCurrentUser()).thenReturn(new UserView().setId(1L));
        when(domainService.addReport(any(), any())).thenReturn(ID);
        when(domainService.getReport(ID)).thenReturn(getReportResponse());

        var response = service.addReport(getReportAddRequest());

        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(DESCRIPTION, response.getDescription());
        assertNotNull(response.getFields());
        assertEquals(CREATED_TIME, response.getCreated());
        assertEquals(MODIFIED_TIME, response.getModified());

        Mockito.reset(dataSetDomainService);
        when(dataSetDomainService.getDataSet(anyLong())).thenReturn(new DataSetResponse().setFields(Collections.singletonList(getDataSetFieldResponse())));
        var request = getReportAddRequest();
        assertThrows(InvalidParametersException.class, () -> service.addReport(request));

        verify(domainService).addReport(any(), any());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void deleteReport() {

        service.deleteReport(new ReportRequest().setId(ID));

        verify(domainService).deleteReport(any());
        verify(olapUserChoiceDomainService).deleteUsersChoiceForReport(anyLong());
        verify(scheduleTaskDomainService).deleteScheduleTaskByReport(anyLong());
        verify(olapConfigurationDomainService).deleteReportOlapConfigurationByReport(anyLong());
        verify(excelTemplateDomainService).removeReportExcelTemplate(anyLong());
        verify(domainService).deleteFavReportsByReportId(anyLong());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void addReportToFavorites() {

        when(userDomainService.getCurrentUser()).thenReturn(new UserView());

        service.addReportToFavorites(new ReportAddFavoritesRequest());

        verify(userDomainService).getCurrentUser();
        verify(domainService).addReportToFavorites(any(), any());

        verifyNoMoreInteractions(userDomainService, domainService);
    }

    @Test
    void getReport() {

        when(domainService.getReport(anyLong())).thenReturn(getReportResponse());
        when(jobDomainService.getLastJobParameters(anyLong(), anyLong())).thenReturn(getReportJobFilterResponse());
        when(jobDomainService.getJobParameters(anyLong())).thenReturn(Collections.emptyList());
        when(userDomainService.getCurrentUser()).thenReturn(new UserView().setId(ID));
        when(filterReportDomainService.getFilterReportData(anyLong())).thenReturn(getFilterData());
        when(filterQueryExecutor.getFieldsValues(any())).thenReturn(new ArrayList<>());

        var response = service.getReport(new ReportRequest().setId(ID));
        assertNotNull(response);
        verify(jobDomainService).getLastJobParameters(anyLong(), anyLong());

        response = service.getReport(new ReportRequest().setId(ID).setJobId(ID));
        assertNotNull(response);
        verify(jobDomainService).getJobParameters(anyLong());

        Mockito.reset(jobDomainService);
        when(jobDomainService.getLastJobParameters(anyLong(), anyLong())).thenThrow(NullPointerException.class);

        response = service.getReport(new ReportRequest().setId(ID));
        assertNotNull(response);

        verify(userDomainService, times(2)).getCurrentUser();
        verify(domainService, times(3)).getReport(anyLong());
        verify(jobDomainService).getLastJobParameters(anyLong(), anyLong());
        verifyNoMoreInteractions(domainService, jobDomainService, userDomainService);
    }

    @Test
    void editReport() {
        var request = getReportEditRequestLondDescription();
        assertThrows(InvalidParametersException.class, () ->
                service.editReport((request)));

        when(domainService.getDeletedFields(any())).thenReturn(Collections.emptyList());
        when(domainService.getReport(anyLong())).thenReturn(getReportResponse());
        when(userDomainService.getCurrentUser()).thenReturn(new UserView().setId(ID));

        var response = service.editReport(getReportEditRequest());
        assertNotNull(response);

        response = service.editReport(getReportEditRequest().setFilterGroup(null));
        assertNotNull(response);

        verify(filterReportDomainService, times(2)).removeFilters(anyLong());
        verify(domainService, times(2)).getDeletedFields(any());
        verify(domainService, times(2)).deleteFields(anyList());
        verify(domainService, times(2)).editReport(any(), any());
        verify(filterReportService).addFilters(any());
        verify(domainService, times(2)).getReport(anyLong());

        verifyNoMoreInteractions(domainService, filterReportDomainService, filterReportService);

    }

    @Test
    void getPivotFieldTypes() {

        when(domainService.getPivotFieldTypes()).thenReturn(Collections.emptyList());

        var response = service.getPivotFieldTypes();
        assertNotNull(response);

        verify(domainService).getPivotFieldTypes();
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void searchFolder() {
        when(folderEntitySearchDomainService.search(any(), any(), any(), any())).thenReturn(new FolderSearchResponse(Collections.emptyList(), Collections.emptyList()));

        var response = service.searchFolder(new FolderSearchRequest());
        assertNotNull(response);

        verify(folderEntitySearchDomainService).search(any(), any(), any(), any());
        verifyNoMoreInteractions(folderEntitySearchDomainService);
    }

    @Test
    void changeParentFolder() {

        when(domainService.changeParentFolder(any())).thenReturn(new ReportFolderResponse());
        when(userDomainService.getCurrentUser()).thenReturn(getCurrentUser());

        var response = service.changeParentFolder(new FolderChangeParentRequest());
        assertNotNull(response);

        verify(domainService).changeParentFolder(any());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void changeParentFolderExceptionTest1() {

        when(userDomainService.getCurrentUser()).thenReturn(getCurrentUser());
        when(folderPermissionsDomainService.getReportFolderPermissionsForRoles(anyList(), anyList()))
                .thenReturn(Collections.singletonList(new FolderRoleResponse(ID, FolderAuthorityEnum.NONE)));
        when(userDomainService.getUserRoles(any(),any(),any())).thenReturn(Collections.singletonList(new RoleView()));

        var request = new FolderChangeParentRequest();

        assertThrows(InvalidParametersException.class, () -> service.changeParentFolder(request));

        verify(userDomainService).getCurrentUser();
        verify(userDomainService).getUserRoles(any(),any(),any());
        verify(folderPermissionsDomainService).getReportFolderPermissionsForRoles(anyList(),any());
        verifyNoMoreInteractions(domainService, userDomainService, folderPermissionsDomainService);
    }

    @Test
    void changeParentFolderExceptionTest2() {

        when(userDomainService.getCurrentUser()).thenReturn(getCurrentUser());
        //noinspection unchecked
        when(folderPermissionsDomainService.getReportFolderPermissionsForRoles(anyList(), anyList()))
                .thenReturn(Collections.emptyList(),Collections.singletonList(new FolderRoleResponse(ID, FolderAuthorityEnum.NONE)));
        when(userDomainService.getUserRoles(any(),any(),any())).thenReturn(Collections.singletonList(new RoleView()));

        var request = new FolderChangeParentRequest();

        assertThrows(InvalidParametersException.class, () -> service.changeParentFolder(request));

        verify(userDomainService).getCurrentUser();
        verify(userDomainService).getUserRoles(any(),any(),any());
        verify(folderPermissionsDomainService, times(2)).getReportFolderPermissionsForRoles(anyList(),any());
        verifyNoMoreInteractions(domainService, userDomainService, folderPermissionsDomainService);
    }

    @Test
    void getFavReports() {
        when(userDomainService.getCurrentUser()).thenReturn(new UserView());
        when(domainService.getFavReports(any())).thenReturn(new FolderResponse().setReports(Collections.emptyList()));

        var response = service.getFavReports();
        assertNotNull(response);

        verify(userDomainService).getCurrentUser();
        verify(domainService).getFavReports(any());
        verifyNoMoreInteractions(userDomainService, domainService);
    }

    @Test
    void deleteReportToFavorites() {

        when(userDomainService.getCurrentUser()).thenReturn(new UserView());
        service.deleteReportToFavorites(new ReportIdRequest());

        verify(userDomainService).getCurrentUser();
        verify(domainService).deleteReportToFavorites(any(), any());
        verifyNoMoreInteractions(userDomainService, domainService);
    }

    @Test
    void getScheduleReportTest1() {

        when(domainService.getReport(anyLong())).thenReturn(getReportResponse());

        var result = service.getScheduleReport(getScheduleReportRequest(ID));

        assertEquals(ID, result.getId());
        assertEquals(ID, result.getDataSetId());
        assertEquals(NAME, result.getName());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(CREATED_TIME, result.getCreated());
        assertEquals(MODIFIED_TIME, result.getModified());
        assertEquals(1, result.getFields().size());

        verify(domainService).getReport(anyLong());
        verify(jobDomainService).getScheduleJobParameters(anyLong());
        verifyNoMoreInteractions(domainService, jobDomainService);
    }

    @Test
    void getScheduleReportTest2() {

        when(domainService.getReport(anyLong())).thenReturn(getReportResponse());
        when(userDomainService.getCurrentUser()).thenReturn(getCurrentUser());

        var result = service.getScheduleReport(getScheduleReportRequest(null));

        assertEquals(ID, result.getId());
        assertEquals(ID, result.getDataSetId());
        assertEquals(NAME, result.getName());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(CREATED_TIME, result.getCreated());
        assertEquals(MODIFIED_TIME, result.getModified());
        assertEquals(1, result.getFields().size());

        verify(domainService).getReport(anyLong());
        verify(jobDomainService).getLastJobParameters(anyLong(), anyLong());
        verify(userDomainService).getCurrentUser();
        verifyNoMoreInteractions(domainService, jobDomainService, userDomainService);
    }


    @Test
    void checkSecurityFilterPropagationTest1() {
        when(securityFilterDomainService.getReportFieldsMapping(anyLong())).thenReturn(new HashMap<>());

        var result = service.checkSecurityFilterPropagation(ID);

        assertEquals("", result);

        verify(securityFilterDomainService).getReportFieldsMapping(anyLong());
        verifyNoMoreInteractions(securityFilterDomainService);
    }

    @Test
    void checkSecurityFilterPropagationTest2() {
        when(securityFilterDomainService.getReportFieldsMapping(anyLong())).thenReturn(getMappingFields());

        var result = service.checkSecurityFilterPropagation(ID);

        assertEquals("Часть полей фильтров безопасности не могут быть размаплены в отчет.", result);

        verify(securityFilterDomainService).getReportFieldsMapping(anyLong());
        verifyNoMoreInteractions(securityFilterDomainService);
    }

    @Test
    void setReportEncryptTest() {

        service.setReportEncrypt(new ReportEncryptRequest(ID, true));

        verify(domainService).setReportEncrypt(any());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void changeReportParentFolderTest() {

        service.changeReportParentFolder(getChangeParentFolderRequest());

        verify(domainService).changeFilterInstanceParentFolder(any());
        verify(permissionCheckerSystem).checkPermissionsOnAllFolders(any(), any(), any());
        verifyNoMoreInteractions(domainService, folderPermissionsDomainService, permissionCheckerSystem);
    }

    @Test
    void copyReportTest() {
        when(userDomainService.getCurrentUser()).thenReturn(getCurrentUser());

        service.copyReport(getChangeParentFolderRequest());

        verify(domainService).copyReport(any(), any());
        verify(permissionCheckerSystem).checkPermissionsOnAllFolders(any(), any(), any());
        verify(userDomainService).getCurrentUser();
        verifyNoMoreInteractions(domainService, permissionCheckerSystem, userDomainService);
    }

    @Test
    void copyReportFolderTest1() {

        when(userDomainService.getCurrentUser()).thenReturn(getCurrentUser());
        when(userDomainService.getUserRoles(any(), any(), any())).thenReturn(Collections.singletonList(new RoleView().setId(ID)));
        when(folderPermissionsDomainService.getReportFolderPermissionsForRoles(anyList(), anyList())).thenReturn(Collections.emptyList());
        when(domainService.copyReportFolder(any(), any())).thenReturn(Collections.singletonList(ID));
        when(domainService.getFolder(any(), any())).thenReturn(getFolderResponse());

        var response = service.copyReportFolder(getCopyFolderRequest()).get(0);

        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(DESCRIPTION, response.getDescription());
        assertNotNull(response.getChildFolders());
        assertNotNull(response.getReports());
        assertEquals(CREATED_TIME, response.getCreated());
        assertEquals(MODIFIED_TIME, response.getModified());

        verify(userDomainService).getCurrentUser();
        verify(userDomainService).getUserRoles(any(), any(), any());
        verify(domainService).copyReportFolder(any(), any());
        verify(folderPermissionsDomainService, times(2)).getReportFolderPermissionsForRoles(anyList(), anyList());
        verifyNoMoreInteractions(userDomainService, domainService, folderPermissionsDomainService);


    }

    @Test
    void copyReportFolderTest2() {

        when(userDomainService.getCurrentUser()).thenReturn(getCurrentUser());
        when(userDomainService.getUserRoles(any(), any(), any())).thenReturn(Collections.singletonList(new RoleView().setId(ID)));
        when(folderPermissionsDomainService.getReportFolderPermissionsForRoles(anyList(), anyList()))
                .thenReturn(Collections.singletonList(new FolderRoleResponse(ID, FolderAuthorityEnum.NONE)));

        var request = getCopyFolderRequest();

        assertThrows(InvalidParametersException.class, () -> service.copyReportFolder(request));


        verify(userDomainService).getCurrentUser();
        verify(userDomainService).getUserRoles(any(), any(), any());
        verifyNoMoreInteractions(domainService, permissionCheckerSystem, userDomainService);
    }

    @SuppressWarnings("unchecked")
    @Test
    void copyReportFolderTest3() {

        when(userDomainService.getCurrentUser()).thenReturn(getCurrentUser());
        when(userDomainService.getUserRoles(any(), any(), any())).thenReturn(Collections.singletonList(new RoleView().setId(ID)));
        when(folderPermissionsDomainService.getReportFolderPermissionsForRoles(anyList(), anyList()))
                .thenReturn(new ArrayList<>(), Collections.singletonList(new FolderRoleResponse(ID, FolderAuthorityEnum.NONE)));

        var request = getCopyFolderRequest();

        assertThrows(InvalidParametersException.class, () -> service.copyReportFolder(request));


        verify(userDomainService).getCurrentUser();
        verify(userDomainService).getUserRoles(any(), any(), any());
        verifyNoMoreInteractions(domainService, permissionCheckerSystem, userDomainService);
    }


    private ReportFolderResponse getFolderResponse() {
        return new ReportFolderResponse()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setChildFolders(new LinkedList<>(List.of(new ReportFolderResponse())))
                .setReports(Collections.singletonList(new ReportResponse()))
                .setCreated(CREATED_TIME)
                .setModified(MODIFIED_TIME);
    }

    private FolderAddRequest getFolderAddRequest() {
        return new FolderAddRequest()
                .setParentId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION);
    }

    private FolderRenameRequest getFolderRenameRequest() {
        return new FolderRenameRequest()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION);
    }

    private ReportResponse getReportResponse() {
        return new ReportResponse()
                .setId(ID)
                .setDataSetId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setFields(Collections.singletonList(new ReportFieldResponse()))
                .setCreated(CREATED_TIME)
                .setModified(MODIFIED_TIME);
    }

    private ReportAddRequest getReportAddRequest() {
        return new ReportAddRequest()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setDataSetId(ID)
                .setFolderId(ID);
    }

    private ReportEditRequest getReportEditRequest() {
        return new ReportEditRequest()
                .setId(ID)
                .setName("name")
                .setDescription("description")
                .setRequirementsLink("url")
                .setFilterGroup(new FilterGroupAddRequest());
    }

    private ReportEditRequest getReportEditRequestLondDescription() {
        return new ReportEditRequest()
                .setId(ID)
                .setName("name")
                .setDescription("**************************************************" +
                        "**************************************************" +
                        "**************************************************" +
                        "**************************************************" +
                        "**************************************************" +
                        "**************************************************")
                .setRequirementsLink("url")
                .setFilterGroup(new FilterGroupAddRequest());
    }

    private List<ReportJobFilterResponse> getReportJobFilterResponse() {
        return Arrays.asList(
                new ReportJobFilterResponse()
                        .setFilterId(0L)
                        .setFilterType(FilterTypeEnum.TOKEN_INPUT)
                        .setParameters(Collections.emptyList()),
                new ReportJobFilterResponse()
                        .setFilterId(1L)
                        .setFilterType(FilterTypeEnum.VALUE_LIST)
                        .setParameters(Collections.emptyList()),
                new ReportJobFilterResponse()
                        .setFilterId(2L)
                        .setFilterType(FilterTypeEnum.HIERARCHY)
                        .setParameters(Collections.emptyList())
        );
    }

    private FilterData getFilterData() {
        return new FilterData(
                null,
                0L,
                FilterTypeEnum.VALUE_LIST,
                null,
                null,
                null,
                null,
                null,
                Collections.singletonList(
                        new FilterFieldData(
                                0L,
                                1,
                                "Name",
                                "Description",
                                "FieldName",
                                null,
                                FilterFieldTypeEnum.CODE_FIELD
                        )));
    }

    private UserView getCurrentUser() {
        return new UserView()
                .setId(ID)
                .setName("TestUser")
                .setDomain(new DomainShortResponse(ID, NAME));
    }

    private DataSetFieldResponse getDataSetFieldResponse() {
        return new DataSetFieldResponse()
                .setTypeName("INTEGER")
                .setIsValid(false);
    }

    private ScheduleReportRequest getScheduleReportRequest(Long scheduleTaskId) {
        return new ScheduleReportRequest(ID, scheduleTaskId);
    }

    private Map<Long, String> getMappingFields() {
        var mappingFields = new HashMap<Long, String>();
        mappingFields.put(1L, null);
        return mappingFields;
    }

    private ChangeParentFolderRequest getChangeParentFolderRequest() {
        return new ChangeParentFolderRequest()
                .setObjIds(Collections.singletonList(ID))
                .setDestFolderId(ID);
    }

    private CopyFolderRequest getCopyFolderRequest() {
        return new CopyFolderRequest()
                .setDestFolderId(ID)
                .setFolderIds(Collections.singletonList(ID))
                .setInheritParentRights(true)
                .setInheritRightsRecursive(true);
    }
}