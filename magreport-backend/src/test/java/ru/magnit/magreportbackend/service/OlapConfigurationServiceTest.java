package ru.magnit.magreportbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.magnit.magreportbackend.domain.folderreport.FolderAuthorityEnum;
import ru.magnit.magreportbackend.dto.inner.UserView;
import ru.magnit.magreportbackend.dto.request.olap.OlapConfigRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapConfigUpdateRequest;
import ru.magnit.magreportbackend.dto.request.olap.ReportOlapConfigAddRequest;
import ru.magnit.magreportbackend.dto.request.olap.ReportOlapConfigRequest;
import ru.magnit.magreportbackend.dto.request.olap.ReportOlapConfigSetShareRequest;
import ru.magnit.magreportbackend.dto.request.olap.UsersReceivedMyJobsRequest;
import ru.magnit.magreportbackend.dto.request.reportjob.ReportJobRequest;
import ru.magnit.magreportbackend.dto.response.folder.FolderNodeResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapAvailableConfigurationsResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapConfigResponse;
import ru.magnit.magreportbackend.dto.response.olap.ReportOlapConfigResponse;
import ru.magnit.magreportbackend.dto.response.permission.FolderPermissionCheckResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportShortResponse;
import ru.magnit.magreportbackend.dto.response.reportjob.ReportJobResponse;
import ru.magnit.magreportbackend.dto.response.user.UserShortInfoResponse;
import ru.magnit.magreportbackend.dto.response.user.UserShortResponse;
import ru.magnit.magreportbackend.service.domain.FolderPermissionsDomainService;
import ru.magnit.magreportbackend.service.domain.JobDomainService;
import ru.magnit.magreportbackend.service.domain.OlapConfigurationDomainService;
import ru.magnit.magreportbackend.service.domain.ReportDomainService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OlapConfigurationServiceTest {

    @Mock
    private OlapConfigurationDomainService olapConfigurationDomainService;
    @Mock
    private UserDomainService userDomainService;
    @Mock
    private JobDomainService jobDomainService;
    @Mock
    private ReportDomainService reportDomainService;
    @Mock
    private FolderPermissionsDomainService folderPermissionsDomainService;

    @InjectMocks
    private OlapConfigurationService service;

    private final Long REPORT_ID = 0L;
    private final Long JOB_ID = 1L;
    private final Long USER_ID = 2L;
    private final Long REPORT_OLAP_CONFIG_ID = 3L;
    private final Long FOLDER_ID = 4L;
    private final Boolean IS_DEFAULT = true;
    private final Boolean IS_SHARED = true;
    private final Boolean IS_CURRENT = true;
    private final LocalDateTime CREATED = LocalDateTime.now();
    private final LocalDateTime MODIFIED = LocalDateTime.now().plusDays(1);


    @Test
    void addOlapReportConfigTest1() {

        when(userDomainService.getCurrentUser()).thenReturn(new UserView());
        when(reportDomainService.getReport(anyLong())).thenReturn(getReportResponse());
        when(folderPermissionsDomainService.checkFolderPermission(any())).thenReturn(new FolderPermissionCheckResponse(FolderAuthorityEnum.WRITE));
        when(olapConfigurationDomainService.updateReportOlapConfiguration(any(), any(), any())).thenReturn(1L);
        when(olapConfigurationDomainService.getReportOlapConfiguration(anyLong())).thenReturn(getReportOlapConfigResponse());

        var result = service.addOlapReportConfig(getReportOlapConfigAddRequest(null));

        assertNotNull(result);

        assertEquals(REPORT_OLAP_CONFIG_ID, result.getReportOlapConfigId());
        assertEquals(JOB_ID, result.getJobId());
        assertEquals(REPORT_ID, result.getReport().id());
        assertEquals(IS_CURRENT, result.getIsCurrent());
        assertEquals(IS_DEFAULT, result.getIsDefault());
        assertEquals(IS_SHARED, result.getIsShare());
        assertEquals(CREATED, result.getCreated());
        assertEquals(MODIFIED, result.getModified());
        assertEquals(USER_ID, result.getUser().id());
        assertEquals(USER_ID, result.getCreator().id());

        assertNotNull(result.getOlapConfig());

        verify(userDomainService).getCurrentUser();
        verify(reportDomainService).getReport(anyLong());
        verify(folderPermissionsDomainService).checkFolderPermission(any());
        verify(olapConfigurationDomainService).updateReportOlapConfiguration(any(), any(), any());
        verify(olapConfigurationDomainService).getReportOlapConfiguration(anyLong());

        verifyNoMoreInteractions(userDomainService, reportDomainService, folderPermissionsDomainService, olapConfigurationDomainService);

    }


    @Test
    void addOlapReportConfigTest2() {

        when(userDomainService.getCurrentUser()).thenReturn(new UserView());
        when(jobDomainService.getJob(anyLong())).thenReturn(getReportJobResponse());
        when(folderPermissionsDomainService.checkFolderPermission(any())).thenReturn(new FolderPermissionCheckResponse(FolderAuthorityEnum.WRITE));
        when(olapConfigurationDomainService.updateReportOlapConfiguration(any(), any(), any())).thenReturn(1L);
        when(olapConfigurationDomainService.getReportOlapConfiguration(anyLong())).thenReturn(getReportOlapConfigResponse());

        var result = service.addOlapReportConfig(getReportOlapConfigAddRequest(JOB_ID));

        assertNotNull(result);

        assertEquals(REPORT_OLAP_CONFIG_ID, result.getReportOlapConfigId());
        assertEquals(JOB_ID, result.getJobId());
        assertEquals(REPORT_ID, result.getReport().id());
        assertEquals(IS_CURRENT, result.getIsCurrent());
        assertEquals(IS_DEFAULT, result.getIsDefault());
        assertEquals(IS_SHARED, result.getIsShare());
        assertEquals(CREATED, result.getCreated());
        assertEquals(MODIFIED, result.getModified());
        assertEquals(USER_ID, result.getUser().id());
        assertEquals(USER_ID, result.getCreator().id());

        assertNotNull(result.getOlapConfig());

        verify(userDomainService).getCurrentUser();
        verify(jobDomainService).getJob(anyLong());
        verify(folderPermissionsDomainService).checkFolderPermission(any());
        verify(olapConfigurationDomainService).updateReportOlapConfiguration(any(), any(), any());
        verify(olapConfigurationDomainService).getReportOlapConfiguration(anyLong());

        verifyNoMoreInteractions(userDomainService, jobDomainService, folderPermissionsDomainService, olapConfigurationDomainService);

    }


    @Test
    void getOlapReportConfigTest() {

        when(olapConfigurationDomainService.getReportOlapConfiguration(anyLong())).thenReturn(getReportOlapConfigResponse());

        var result = service.getOlapReportConfig(new ReportOlapConfigRequest().setReportOlapConfigId(REPORT_OLAP_CONFIG_ID));

        assertNotNull(result);

        assertEquals(REPORT_OLAP_CONFIG_ID, result.getReportOlapConfigId());
        assertEquals(JOB_ID, result.getJobId());
        assertEquals(REPORT_ID, result.getReport().id());
        assertEquals(IS_CURRENT, result.getIsCurrent());
        assertEquals(IS_DEFAULT, result.getIsDefault());
        assertEquals(IS_SHARED, result.getIsShare());
        assertEquals(CREATED, result.getCreated());
        assertEquals(MODIFIED, result.getModified());
        assertEquals(USER_ID, result.getUser().id());
        assertEquals(USER_ID, result.getCreator().id());

        assertNotNull(result.getOlapConfig());

        verify(olapConfigurationDomainService).getReportOlapConfiguration(anyLong());
        verifyNoMoreInteractions(olapConfigurationDomainService);


    }

    @Test
    void deleteOlapConfigTest() {

        when(userDomainService.getCurrentUser()).thenReturn(new UserView().setId(USER_ID));

        service.deleteOlapConfig(new OlapConfigRequest());

        verify(userDomainService).getCurrentUser();
        verify(olapConfigurationDomainService).deleteOlapConfiguration(any(), any());

        verifyNoMoreInteractions(userDomainService, olapConfigurationDomainService);
    }

    @Test
    void deleteOlapReportConfigTest() {

        when(userDomainService.getCurrentUser()).thenReturn(new UserView().setId(USER_ID));

        service.deleteOlapReportConfig(new ReportOlapConfigRequest());

        verify(userDomainService).getCurrentUser();
        verify(olapConfigurationDomainService).deleteReportOlapConfiguration(any(), any());

        verifyNoMoreInteractions(userDomainService, olapConfigurationDomainService);

    }

    @Test
    void getUsersReceivedMyJobsTest() {

        when(userDomainService.getCurrentUser()).thenReturn(new UserView());
        when(olapConfigurationDomainService.getListUsersReceivedAuthorJob(any(), any())).thenReturn(Collections.singletonList(getUserShortInfoResponse()));

        var result = service.getUsersReceivedMyJobs(new UsersReceivedMyJobsRequest());

        assertFalse(result.isEmpty());
        assertNotNull(result.get(0));

        verify(userDomainService).getCurrentUser();
        verify(olapConfigurationDomainService).getListUsersReceivedAuthorJob(any(), any());
        verifyNoMoreInteractions(userDomainService, olapConfigurationDomainService);

    }

    @Test
    void setDefaultReportConfigurationTest() {

        service.setDefaultReportConfiguration(new ReportOlapConfigRequest());
        verify(olapConfigurationDomainService).setDefaultReportConfiguration(any());
        verifyNoMoreInteractions(olapConfigurationDomainService);
    }

    @Test
    void setSharedStatusReportConfigurationTest() {

        when(userDomainService.getCurrentUser()).thenReturn(new UserView());
        service.setSharedStatusReportConfiguration(new ReportOlapConfigSetShareRequest());

        verify(userDomainService).getCurrentUser();
        verify(olapConfigurationDomainService).updateSharedStatusOlapReportConfig(any(), any());
        verifyNoMoreInteractions(userDomainService, olapConfigurationDomainService);

    }

    @Test
    void getAvailableConfigurationsForJobTest() {

        when(jobDomainService.getJob(any())).thenReturn(getReportJobResponse());
        when(userDomainService.getCurrentUser()).thenReturn(new UserView());
        when(olapConfigurationDomainService.getAvailableReportOlapConfigurationForJob(any(), any(), any())).thenReturn(new OlapAvailableConfigurationsResponse());

        var result = service.getAvailableConfigurationsForJob(new ReportJobRequest());

        assertNotNull(result);

        verify(jobDomainService).getJob(any());
        verify(userDomainService).getCurrentUser();
        verify(olapConfigurationDomainService).getAvailableReportOlapConfigurationForJob(any(), any(), any());

        verifyNoMoreInteractions(jobDomainService, userDomainService, olapConfigurationDomainService);

    }

    @Test
    void getCurrentConfigurationTest() {

        when(jobDomainService.getJob(any())).thenReturn(getReportJobResponse());
        when(userDomainService.getCurrentUser()).thenReturn(new UserView());
        when(olapConfigurationDomainService.getCurrentConfiguration(any(), any(), any())).thenReturn(1L);
        when(olapConfigurationDomainService.getReportOlapConfiguration(anyLong())).thenReturn(getReportOlapConfigResponse());

        var result = service.getCurrentConfiguration(new ReportJobRequest());

        assertNotNull(result);

        assertEquals(REPORT_OLAP_CONFIG_ID, result.getReportOlapConfigId());
        assertEquals(JOB_ID, result.getJobId());
        assertEquals(REPORT_ID, result.getReport().id());
        assertEquals(IS_CURRENT, result.getIsCurrent());
        assertEquals(IS_DEFAULT, result.getIsDefault());
        assertEquals(IS_SHARED, result.getIsShare());
        assertEquals(CREATED, result.getCreated());
        assertEquals(MODIFIED, result.getModified());
        assertEquals(USER_ID, result.getUser().id());
        assertEquals(USER_ID, result.getCreator().id());

        assertNotNull(result.getOlapConfig());

        verify(jobDomainService).getJob(any());
        verify(userDomainService).getCurrentUser();

    }


    private ReportOlapConfigAddRequest getReportOlapConfigAddRequest(Long jobId) {
        return new ReportOlapConfigAddRequest()
                .setReportId(REPORT_ID)
                .setReportOlapConfigId(REPORT_OLAP_CONFIG_ID)
                .setJobId(jobId)
                .setUserId(USER_ID)
                .setIsDefault(IS_DEFAULT)
                .setIsShare(IS_SHARED)
                .setIsCurrent(IS_CURRENT)
                .setOlapConfig(new OlapConfigUpdateRequest());
    }

    private ReportOlapConfigResponse getReportOlapConfigResponse() {
        return new ReportOlapConfigResponse()
                .setJobId(JOB_ID)
                .setReportOlapConfigId(REPORT_OLAP_CONFIG_ID)
                .setReport(new ReportShortResponse(REPORT_ID, null, null))
                .setIsCurrent(IS_CURRENT)
                .setIsDefault(IS_DEFAULT)
                .setIsShare(IS_SHARED)
                .setOlapConfig(new OlapConfigResponse())
                .setUser(new UserShortResponse(USER_ID, null, null))
                .setCreator(new UserShortResponse(USER_ID, null, null))
                .setCreated(CREATED)
                .setModified(MODIFIED);
    }

    private ReportResponse getReportResponse() {
        return new ReportResponse()
                .setPath(Collections.singletonList(
                        new FolderNodeResponse(FOLDER_ID, null, null, null, null, null)));
    }

    private ReportJobResponse getReportJobResponse() {
        var result = new ReportJobResponse();
        result.setReport(new ReportShortResponse(REPORT_ID, FOLDER_ID, ""));
        return result;
    }

    private UserShortInfoResponse getUserShortInfoResponse() {
        return new UserShortInfoResponse()
                .setDomain("")
                .setLogin("")
                .setFullName("");
    }
}
