package ru.magnit.magreportbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.magnit.magreportbackend.domain.datasource.DataSourceTypeEnum;
import ru.magnit.magreportbackend.domain.enums.BinaryBooleanOperations;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterFieldTypeEnum;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterOperationTypeEnum;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTypeEnum;
import ru.magnit.magreportbackend.domain.folderreport.FolderAuthorityEnum;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobStateEnum;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobStatusEnum;
import ru.magnit.magreportbackend.dto.inner.RoleView;
import ru.magnit.magreportbackend.dto.inner.UserView;
import ru.magnit.magreportbackend.dto.inner.datasource.DataSourceData;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportData;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportFilterGroupData;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportJobData;
import ru.magnit.magreportbackend.dto.request.reportjob.ExcelReportRequest;
import ru.magnit.magreportbackend.dto.request.reportjob.ReportJobAddRequest;
import ru.magnit.magreportbackend.dto.request.reportjob.ReportJobCommentRequest;
import ru.magnit.magreportbackend.dto.request.reportjob.ReportJobFilterRequest;
import ru.magnit.magreportbackend.dto.request.reportjob.ReportJobHistoryRequestFilter;
import ru.magnit.magreportbackend.dto.request.reportjob.ReportJobRequest;
import ru.magnit.magreportbackend.dto.request.reportjob.ReportJobShareRequest;
import ru.magnit.magreportbackend.dto.request.reportjob.ReportPageRequest;
import ru.magnit.magreportbackend.dto.request.user.UserRequest;
import ru.magnit.magreportbackend.dto.response.filterreport.FilterGroupResponse;
import ru.magnit.magreportbackend.dto.response.filterreport.FilterReportFieldResponse;
import ru.magnit.magreportbackend.dto.response.filterreport.FilterReportResponse;
import ru.magnit.magreportbackend.dto.response.folder.FolderRoleResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportJobFilterResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportShortResponse;
import ru.magnit.magreportbackend.dto.response.reportjob.ReportJobMetadataResponse;
import ru.magnit.magreportbackend.dto.response.reportjob.ReportJobResponse;
import ru.magnit.magreportbackend.dto.response.reportjob.ReportPageResponse;
import ru.magnit.magreportbackend.dto.response.reportjob.ReportSqlQueryResponse;
import ru.magnit.magreportbackend.dto.response.user.UserResponse;
import ru.magnit.magreportbackend.dto.response.user.UserShortResponse;
import ru.magnit.magreportbackend.dto.tuple.Tuple;
import ru.magnit.magreportbackend.dto.tuple.TupleValue;
import ru.magnit.magreportbackend.exception.InvalidParametersException;
import ru.magnit.magreportbackend.exception.PermissionDeniedException;
import ru.magnit.magreportbackend.exception.ReportExportException;
import ru.magnit.magreportbackend.service.domain.AvroReportDomainService;
import ru.magnit.magreportbackend.service.domain.ExcelReportDomainService;
import ru.magnit.magreportbackend.service.domain.ExcelTemplateDomainService;
import ru.magnit.magreportbackend.service.domain.FilterReportDomainService;
import ru.magnit.magreportbackend.service.domain.FolderDomainService;
import ru.magnit.magreportbackend.service.domain.FolderPermissionsDomainService;
import ru.magnit.magreportbackend.service.domain.JobDomainService;
import ru.magnit.magreportbackend.service.domain.OlapConfigurationDomainService;
import ru.magnit.magreportbackend.service.domain.OlapUserChoiceDomainService;
import ru.magnit.magreportbackend.service.domain.ReportDomainService;
import ru.magnit.magreportbackend.service.domain.ReportJobUserDomainService;
import ru.magnit.magreportbackend.service.domain.TokenService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ru.magnit.magreportbackend.domain.reportjob.ReportJobStatusEnum.COMPLETE;
import static ru.magnit.magreportbackend.domain.reportjob.ReportJobStatusEnum.EXPORT;
import static ru.magnit.magreportbackend.domain.reportjob.ReportJobStatusEnum.FAILED;
import static ru.magnit.magreportbackend.domain.reportjob.ReportJobStatusEnum.RUNNING;

@ExtendWith(MockitoExtension.class)
class ReportJobServiceTest {

    private static final Long ID = 1L;
    private static final Long REPORT_ID = 2L;
    private static final Long JOB_ID = 5L;
    private static final Long FILTER_ID = 10L;
    private static final Long TOTAL_ROW = 100L;
    private static final String USERNAME = "NAME";

    @InjectMocks
    private ReportJobService service;

    @Mock
    private JobDomainService jobDomainService;

    @Mock
    private ExcelReportDomainService excelReportDomainService;

    @Mock
    private AvroReportDomainService avroReportDomainService;

    @Mock
    private ReportDomainService reportDomainService;

    @Mock
    private FilterReportDomainService filterReportDomainService;

    @Mock
    private ExcelTemplateDomainService excelTemplateDomainService;

    @Mock
    private FolderDomainService folderDomainService;

    @Mock
    private FolderPermissionsDomainService folderPermissionsDomainService;

    @Mock
    private UserDomainService userDomainService;

    @Mock
    private TokenService tokenService;

    @Mock
    private OlapUserChoiceDomainService olapUserChoiceDomainService;

    @Mock
    private ReportJobUserDomainService reportJobUserDomainService;
    @Mock
    private OlapConfigurationDomainService olapConfigurationDomainService;


    @Test
    void getExcelReport() {

        //job not complete
        ExcelReportRequest request = spy(getExcelReportRequest());
        ReportJobData jobData = getReportJobData(false);

        when(jobDomainService.getJobData(ID)).thenReturn(jobData);
        when(excelTemplateDomainService.getTemplatePathForReport(ID, 1L)).thenReturn("");

        var result = service.getExcelReport(1L, 1L);

        assertNull(result);
        verify(jobDomainService).getJobData(ID);

        verify(excelReportDomainService).getExcelReport(any(), anyString(), anyLong());
        verify(excelReportDomainService).createExcelReport(any(), any(), anyLong());
        verifyNoMoreInteractions(jobDomainService, excelReportDomainService, avroReportDomainService, reportDomainService, filterReportDomainService);


        Mockito.reset(request, jobDomainService, excelReportDomainService);

        //job complete
        jobData = getReportJobData(true);

        when(jobDomainService.getJobData(ID)).thenReturn(jobData);
        when(excelReportDomainService.getExcelReport(any(), anyString(), anyLong())).thenReturn(mock(StreamingResponseBody.class));

        result = service.getExcelReport(1L, 1L);

        assertNotNull(result);

        verify(jobDomainService).getJobData(ID);
        verify(excelReportDomainService).getExcelReport(any(), anyString(), anyLong());
        verify(excelReportDomainService).createExcelReport(any(), any(), anyLong());
        verifyNoMoreInteractions(jobDomainService, excelReportDomainService, avroReportDomainService, reportDomainService, filterReportDomainService);

    }

    @Test
    void addJob() {

        // no job filters

        ReportJobAddRequest request = spy(getReportJobAddRequest());
        final var permissionsResponse = new FolderRoleResponse(1L, FolderAuthorityEnum.WRITE);

        when(reportDomainService.getReport(anyLong())).thenReturn(getReportResponse());
        when(userDomainService.getCurrentUser()).thenReturn(new UserView().setId(1L));
        when(jobDomainService.addJob(any())).thenReturn(ID);
        when(jobDomainService.getJob(anyLong())).thenReturn(getReportJobResponse(COMPLETE));
        when(folderDomainService.getReportsFolders(anyLong())).thenReturn(Collections.singletonList(1L));
        when(folderPermissionsDomainService.getFoldersReportPermissionsForRoles(any(), any())).thenReturn(Collections.singletonList(permissionsResponse));
        when(userDomainService.getCurrentUserRoles(any())).thenReturn(Collections.emptyList());
        when(jobDomainService.getActiveJobs(anyLong(),anyLong(),any())).thenReturn(Collections.singletonList(getReportJobResponse(RUNNING)));
        when(jobDomainService.getJobParameters(anyLong())).thenReturn(Collections.singletonList(new ReportJobFilterResponse().setParameters(Collections.emptyList())));
        when(filterReportDomainService.getCleanedValueListValues(any())).thenReturn(Collections.singletonList(new Tuple()));

        final var result = service.addJob(request);

        assertNotNull(result);

        verify(reportDomainService).getReport(anyLong());
        verify(jobDomainService).addJob(any());
        verify(jobDomainService).getJob(anyLong());
        verify(userDomainService, times(2)).getCurrentUser();
        verify(folderDomainService).getReportsFolders(anyLong());
        verify(folderPermissionsDomainService).getFoldersReportPermissionsForRoles(any(),any());
        verify(userDomainService).getCurrentUserRoles(any());
        verify(jobDomainService).getActiveJobs(anyLong(),anyLong(),any());
        verify(jobDomainService).getJobParameters(anyLong());
        verifyNoMoreInteractions(reportDomainService,jobDomainService,userDomainService,folderDomainService,folderPermissionsDomainService);
    }



    @Test
    void addJobNoMatchedRequestParameters() {
        // one job filter, not matchedRequestParameters
        ReportResponse reportResponse = mock(ReportResponse.class);
        FilterReportResponse filter = getFilterReportResponse(true);
        ReportJobAddRequest request = spy(getReportJobAddRequest());
        final var permissionsResponse = new FolderRoleResponse(1L, FolderAuthorityEnum.WRITE);

        when(reportDomainService.getReport(anyLong())).thenReturn(reportResponse);
        when(folderDomainService.getReportsFolders(anyLong())).thenReturn(Collections.emptyList());
        when(userDomainService.getCurrentUserRoles(any())).thenReturn(Collections.emptyList());

        assertThrows(PermissionDeniedException.class, () -> service.addJob(request));

        verify(request).getReportId();
        verify(reportDomainService).getReport(anyLong());

        verifyNoMoreInteractions(request, jobDomainService, excelReportDomainService, avroReportDomainService, reportDomainService, filterReportDomainService);

    }

    @Test
    void addJobEmptyValues() {
        // one job filter, has matched request parameters, empty cleaned value list

        ReportJobAddRequest request = getReportJobAddRequest();
        ReportResponse reportResponse = mock(ReportResponse.class);
        final var permissionsResponse = new FolderRoleResponse(1L, FolderAuthorityEnum.WRITE);

        when(reportDomainService.getReport(any())).thenReturn(reportResponse);
        when(folderDomainService.getReportsFolders(anyLong())).thenReturn(Collections.emptyList());
        when(userDomainService.getCurrentUserRoles(any())).thenReturn(Collections.emptyList());

        assertThrows(PermissionDeniedException.class, () -> service.addJob(request));

        verify(reportDomainService).getReport(any());
        verifyNoMoreInteractions(reportDomainService, filterReportDomainService, jobDomainService);
    }

    @Test
    void getJobTest1() {

        when(jobDomainService.getJob(anyLong())).thenReturn(getReportJobResponse(COMPLETE));

        var response = service.getJob(ID);
        assertNotNull(response);

        verify(jobDomainService).getJob(anyLong());
        verifyNoMoreInteractions(jobDomainService);
    }


    @Test
    void getJobTest2() {

        when(jobDomainService.getJob(anyLong())).thenReturn(getReportJobResponse(RUNNING));

        var response = service.getJob(getReportJobRequest());
        assertNotNull(response);

        verify(jobDomainService).getJob(anyLong());
        verifyNoMoreInteractions(jobDomainService);
    }

    @Test
    void checkAccessForJobTest1() {
        when(userDomainService.getCurrentUser()).thenReturn(new UserView());
        when(jobDomainService.getJob(anyLong())).thenReturn(getReportJobResponse(COMPLETE));
        when(userDomainService.getCurrentUserRoles(null)).thenReturn(Collections.singletonList(new RoleView().setName("ADMIN")));

        assertNotNull(service.getJob(getReportJobRequest()));

        verify(jobDomainService).getJob(anyLong());
        verifyNoMoreInteractions(jobDomainService);
    }

    @Test
    void checkAccessForJobTest2() {
        when(userDomainService.getCurrentUser()).thenReturn(new UserView().setName(USERNAME));
        when(jobDomainService.getJob(anyLong())).thenReturn(getReportJobResponse(COMPLETE));

        var request = getReportJobRequest();

        assertThrows(PermissionDeniedException.class, () -> service.getJob(request));

        verify(jobDomainService, times(2)).getJob(anyLong());
        verify(jobDomainService).getJobUsers(anyLong());
        verifyNoMoreInteractions(jobDomainService);
    }

    @Test
    void checkAccessForJobTest3() {
        when(userDomainService.getCurrentUser()).thenReturn(new UserView().setName(""));
        when(jobDomainService.getJob(anyLong())).thenReturn(getReportJobResponse(COMPLETE));

        var request = getReportJobRequest();

        assertThrows(PermissionDeniedException.class, () -> service.getJob(request));

        verify(jobDomainService, times(2)).getJob(anyLong());
        verify(jobDomainService).getJobUsers(anyLong());
        verifyNoMoreInteractions(jobDomainService);
    }

    @Test
    void getMyJobs() {

        when(jobDomainService.getMyJobs()).thenReturn(Collections.singletonList(getReportJobResponse(COMPLETE)));
        when(userDomainService.getCurrentUser()).thenReturn(new UserView().setId(ID));
        when(reportJobUserDomainService.getShortUsersJob(anyLong())).thenReturn(Collections.emptyList());

        final var result = service.getMyJobs(null);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(jobDomainService).getMyJobs();
        verify(userDomainService).getCurrentUser();
        verify(reportJobUserDomainService).getShortUsersJob(anyLong());

        verifyNoMoreInteractions(jobDomainService, excelReportDomainService, avroReportDomainService, reportDomainService, filterReportDomainService);

    }

    @Test
    void getAllJobs() {

        when(jobDomainService.getAllJobs()).thenReturn(Collections.singletonList(getReportJobResponse(COMPLETE)));

        final var result = service.getAllJobs(null);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(jobDomainService).getAllJobs();

        verifyNoMoreInteractions(jobDomainService, excelReportDomainService, avroReportDomainService, reportDomainService, filterReportDomainService);

    }

    @Test
    void getAllJobsWithFilterTest1() {

        when(jobDomainService.getAllJobs()).thenReturn(Collections.singletonList(getReportJobResponse(COMPLETE)));

        final var result = service.getAllJobs(getReportJobHistoryRequestFilter(null, null, null, null, null));

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(jobDomainService).getAllJobs();

        verifyNoMoreInteractions(jobDomainService, excelReportDomainService, avroReportDomainService, reportDomainService, filterReportDomainService);

    }

    @Test
    void getAllJobsWithFilterTest2() {

        when(jobDomainService.getAllJobs()).thenReturn(Collections.singletonList(getReportJobResponse(COMPLETE)));

        final var result = service.getAllJobs(getReportJobHistoryRequestFilter(Collections.singletonList(ID + 1), null, null, null, null));

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(jobDomainService).getAllJobs();

        verifyNoMoreInteractions(jobDomainService, excelReportDomainService, avroReportDomainService, reportDomainService, filterReportDomainService);

    }

    @Test
    void getAllJobsWithFilterTest3() {

        when(jobDomainService.getAllJobs()).thenReturn(Collections.singletonList(getReportJobResponse(COMPLETE)));

        final var result = service.getAllJobs(getReportJobHistoryRequestFilter(null, Collections.singletonList(ID + 1), null, null, null));

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(jobDomainService).getAllJobs();

        verifyNoMoreInteractions(jobDomainService, excelReportDomainService, avroReportDomainService, reportDomainService, filterReportDomainService);

    }

    @Test
    void getAllJobsWithFilterTest4() {

        when(jobDomainService.getAllJobs()).thenReturn(Collections.singletonList(getReportJobResponse(COMPLETE)));

        final var result = service.getAllJobs(getReportJobHistoryRequestFilter(null, null, Collections.singletonList(FAILED), null, null));

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(jobDomainService).getAllJobs();

        verifyNoMoreInteractions(jobDomainService, excelReportDomainService, avroReportDomainService, reportDomainService, filterReportDomainService);
    }

    @Test
    void getAllJobsWithFilterTest5() {

        when(jobDomainService.getAllJobs()).thenReturn(Collections.singletonList(getReportJobResponse(COMPLETE)));

        final var result = service.getAllJobs(getReportJobHistoryRequestFilter(null, null, null, LocalDateTime.now().minusDays(1), null));

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(jobDomainService).getAllJobs();

        verifyNoMoreInteractions(jobDomainService, excelReportDomainService, avroReportDomainService, reportDomainService, filterReportDomainService);
    }

    @Test
    void getReportPage() {

        //job not complete
        ReportPageRequest request = spy(getReportPageRequest());
        ReportJobData jobData = getReportJobData(false);

        when(jobDomainService.getJobData(anyLong())).thenReturn(jobData);

        var result = service.getReportPage(request);

        assertNull(result);

        verify(request).getJobId();
        verify(jobDomainService).getJobData(anyLong());

        verifyNoMoreInteractions(request, jobDomainService, excelReportDomainService, avroReportDomainService, reportDomainService, filterReportDomainService);


        Mockito.reset(request, jobDomainService, avroReportDomainService);

        //job complete
        jobData = getReportJobData(true);

        when(userDomainService.getCurrentUser()).thenReturn(new UserView());
        when(jobDomainService.getJobData(anyLong())).thenReturn(jobData);
        when(avroReportDomainService.getPage(any(), anyLong(), anyLong())).thenReturn(getReportPageResponse());

        result = service.getReportPage(request);

        assertNotNull(result);

        verify(request).getJobId();
        verify(request).getPageNumber();
        verify(request).getRowsPerPage();
        verify(jobDomainService).getJobData(anyLong());
        verify(avroReportDomainService).getPage(any(), anyLong(), anyLong());

        verifyNoMoreInteractions(request, jobDomainService, excelReportDomainService, avroReportDomainService, reportDomainService, filterReportDomainService);

    }

    @Test
    void cancelJob() {

        ReportJobRequest request = spy(getReportJobRequest());

        when(jobDomainService.getJob(anyLong())).thenReturn(getReportJobResponse(COMPLETE));

        final var result = service.cancelJob(request);

        assertNotNull(result);

        verify(request, times(2)).getJobId();
        verify(jobDomainService).cancelJob(anyLong());
        verify(jobDomainService).getJob(anyLong());

        verifyNoMoreInteractions(request, jobDomainService, excelReportDomainService, avroReportDomainService, reportDomainService, filterReportDomainService);
    }

    @Test
    void createExcelReport() {

        when(jobDomainService.getJobData(any())).thenReturn(getReportJobData(false));
        when(excelTemplateDomainService.getTemplatePathForReport(anyLong(), anyLong())).thenReturn("path");
        when(tokenService.getToken(any(), any())).thenReturn("token");
        when(jobDomainService.getJob(anyLong())).thenReturn(getReportJobResponse(COMPLETE));

        var response = service.createExcelReport(new ExcelReportRequest().setId(ID).setExcelTemplateId(ID));
        assertNotNull(response);

        verify(jobDomainService).getJobData(any());
        verify(excelTemplateDomainService).getTemplatePathForReport(anyLong(), anyLong());
        verify(tokenService).getToken(any(), any());

        verifyNoMoreInteractions(jobDomainService, excelTemplateDomainService, tokenService);
    }

    @Test
    void createExcelReportExceptionTest() {

        when(jobDomainService.getJob(anyLong())).thenReturn(getReportJobResponse(EXPORT));
        when(excelTemplateDomainService.getDefaultExcelTemplateToReport(any())).thenReturn(ID);

        var request = getExcelReportRequest();

        assertThrows(ReportExportException.class, () -> service.createExcelReport(request));

        verify(jobDomainService).getJob(anyLong());
        verify(excelTemplateDomainService).getDefaultExcelTemplateToReport(any());
        verifyNoMoreInteractions(jobDomainService, excelTemplateDomainService);

    }

    @Test
    void getReportSize() {

        when(jobDomainService.getJobData(any())).thenReturn(getReportJobData(false));
        when(excelReportDomainService.getReportSize(anyLong(), anyLong())).thenReturn(1000L);

        var response = service.getReportSize(ID, ID);
        assertEquals(1000L, response);

        verify(jobDomainService).getJobData(any());
        verify(excelReportDomainService).getReportSize(anyLong(), anyLong());
        verifyNoMoreInteractions(jobDomainService, excelReportDomainService);
    }

    @Test
    void saveExcelReport() {

        when(jobDomainService.getJobData(any())).thenReturn(getReportJobData(false));
        when(excelTemplateDomainService.getTemplatePathForReport(anyLong(), anyLong())).thenReturn("path");
        when(excelTemplateDomainService.getDefaultExcelTemplateToReport(anyLong())).thenReturn(ID);

        service.saveExcelReport(getExcelReportRequest().setExcelTemplateId(null));

        verify(jobDomainService).getJobData(any());
        verify(excelTemplateDomainService).getTemplatePathForReport(anyLong(), anyLong());
        verify(excelReportDomainService).saveReportToExcel(any(), any(), anyLong());
        verify(excelReportDomainService).moveReportToRms(anyLong(), anyLong(), any(Boolean.class));
        verify(excelTemplateDomainService).getDefaultExcelTemplateToReport(anyLong());
        verifyNoMoreInteractions(jobDomainService, excelTemplateDomainService, excelReportDomainService);

    }

    @Test
    void deleteJob() {

        service.deleteJob(getReportJobRequest());

        verify(jobDomainService).deleteJob(anyLong());
        verifyNoMoreInteractions(jobDomainService);
    }

    @Test
    void deleteAllJobs() {

        service.deleteAllJobs();

        verify(jobDomainService).deleteAllJobs();
        verifyNoMoreInteractions(jobDomainService);
    }

    @Test
    void getSqlQuery() {

        when(jobDomainService.getSqlQuery(anyLong())).thenReturn(new ReportSqlQueryResponse(ID, "SqlQuery"));

        var response = service.getSqlQuery(getReportJobRequest());
        assertNotNull(response);

        verify(jobDomainService).getSqlQuery(anyLong());
        verifyNoMoreInteractions(jobDomainService);
    }

    @Test
    void checkDateParameters() {

        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service, "checkDateParameters", getReportJobAddRequest(), getReportResponse()));
    }

    @Test
    void getExcelReportPathTest() {

        Path path = Path.of("");

        when(jobDomainService.getJobData(anyLong())).thenReturn(getReportJobData(true));
        when(excelTemplateDomainService.getTemplatePathForReport(anyLong(), anyLong())).thenReturn("");
        when(excelReportDomainService.getExcelReportPath(anyLong(), anyLong())).thenReturn(path);

        assertEquals(path, service.getExcelReportPath(ID, ID));

        verify(jobDomainService).getJobData(anyLong());
        verify(excelTemplateDomainService).getTemplatePathForReport(anyLong(), anyLong());
        verify(excelReportDomainService).createExcelReport(any(), any(), any());
        verify(jobDomainService).updateJobStats(anyLong(), anyBoolean(), anyBoolean(), anyBoolean());
        verify(excelReportDomainService).getExcelReportPath(anyLong(), anyLong());
        verifyNoMoreInteractions(jobDomainService, excelTemplateDomainService, excelReportDomainService);

    }

    @Test
    void getPathToExcelReportTest() {

        final var path = Path.of("");
        when(excelReportDomainService.getExcelReportPath(anyLong(), anyLong())).thenReturn(path);

        var response = service.getPathToExcelReport(getExcelReportRequest());

        assertEquals(path, response);

        verify(excelReportDomainService).getExcelReportPath(anyLong(), anyLong());
        verifyNoMoreInteractions(excelTemplateDomainService);

    }

    @Test
    void getMetaDataTest() {

        when(jobDomainService.getJobMetaData(anyLong())).thenReturn(getReportJobMetadataResponse());

        var response = service.getMetaData(getReportJobRequest());

        assertEquals(ID, response.id());
        assertEquals(TOTAL_ROW, response.totalRows());
        assertEquals(0, response.fields().size());

        verify(jobDomainService).getJobMetaData(anyLong());
        verifyNoMoreInteractions(jobDomainService);
    }

    @Test
    void shareJobTest() {

        when(userDomainService.getCurrentUser()).thenReturn(new UserView().setId(ID).setName(USERNAME));
        when(jobDomainService.getJob(anyLong())).thenReturn(getReportJobResponse(COMPLETE));
        when(userDomainService.getUserResponse(anyString(), anyString())).thenReturn(new UserResponse());

        service.shareJob(getReportJobShareRequest());

        verify(userDomainService).getCurrentUser();
        verify(jobDomainService).getJob(anyLong());
        verify(reportJobUserDomainService).addUsersJob(any(), any(), any());
        verify(olapConfigurationDomainService).createCurrentConfigurationForUsers(any(), anyLong(), anyLong());
        verify(jobDomainService).updateJobStats(anyLong(), anyBoolean(), anyBoolean(), anyBoolean());
        verifyNoMoreInteractions(userDomainService, jobDomainService, reportJobUserDomainService, olapConfigurationDomainService);
    }

    @Test
    void shareJobExceptionTest() {

        when(userDomainService.getCurrentUser()).thenReturn(new UserView().setId(ID).setName(""));
        when(jobDomainService.getJob(anyLong())).thenReturn(getReportJobResponse(COMPLETE));

        var request = getReportJobShareRequest();

        assertThrows(InvalidParametersException.class, () -> service.shareJob(request));

        verify(userDomainService).getCurrentUser();
        verify(jobDomainService).getJob(anyLong());
        verifyNoMoreInteractions(userDomainService, jobDomainService, reportJobUserDomainService, olapConfigurationDomainService);
    }

    @Test
    void getUsersJob() {

        when(reportJobUserDomainService.getUsersJob(anyLong())).thenReturn(Collections.singletonList(new UserResponse()));

        var response = service.getUsersJob(getReportJobRequest());
        assertEquals(1, response.size());

        verify(reportJobUserDomainService).getUsersJob(anyLong());
        verifyNoMoreInteractions(reportJobUserDomainService);
    }

    @Test
    void addReportJobCommentTest() {

        when(userDomainService.getCurrentUser()).thenReturn(new UserView().setId(ID));

        service.addReportJobComment(new ReportJobCommentRequest().setJobId(ID).setComment(""));

        verify(userDomainService).getCurrentUser();
        verify(jobDomainService).addReportJobComment(anyLong(), anyLong(), anyString());
        verifyNoMoreInteractions(userDomainService, jobDomainService);
    }

    @Test
    void getHistoryTest() {

        when(jobDomainService.getJobStatHistory(anyLong())).thenReturn(Collections.emptyList());

        var response = service.getHistory(getReportJobRequest());

        assertEquals(0, response.history().size());

        verify(jobDomainService).getJobStatHistory(anyLong());
        verifyNoMoreInteractions(jobDomainService);

    }

    @Test
    void getAllScheduledReportsTest() {

        when(jobDomainService.getAllScheduledReports()).thenReturn(Collections.emptyList());

        var response = service.getAllScheduledReports();

        assertEquals(0, response.size());

        verify(jobDomainService).getAllScheduledReports();
        verifyNoMoreInteractions(jobDomainService);

    }

    @Test
    void streamReportTest() {

        when(jobDomainService.getJobData(anyLong())).thenReturn(getReportJobData(true));

        service.streamReport(new ResponseBodyEmitter(), ID);

        verify(jobDomainService).getJobData(anyLong());
        verify(avroReportDomainService).streamReport(any(), any());
        verifyNoMoreInteractions(jobDomainService, avroReportDomainService);
    }

    @Test
    void isSameParametersTest(){
      assertDoesNotThrow(() ->
              ReflectionTestUtils.invokeMethod(service, "isSameParameters", Collections.emptyList(), Collections.emptyList()));
    }

    private ReportJobData getReportJobData(boolean isComplete) {
        return new ReportJobData(1L,
                2L,
                3L,
                5L,
                4L,
                "User",
                isComplete ? COMPLETE.getId() : ReportJobStatusEnum.RUNNING.getId(),
                2L,
                3L,
                isComplete,
                new DataSourceData(1L, DataSourceTypeEnum.TERADATA, "url", "user", "pwd", Short.valueOf("1")),
                new ReportData(1L, "name", "desc", "schema", "table", Collections.emptyList(), new ReportFilterGroupData(1L, 1L, "test code", "test code", BinaryBooleanOperations.AND, Collections.emptyList(), Collections.emptyList()), true),
                Collections.emptyList(),
                Collections.emptyList());
    }

    private ExcelReportRequest getExcelReportRequest() {
        return new ExcelReportRequest()
                .setId(ID)
                .setExcelTemplateId(ID);
    }

    private ReportJobRequest getReportJobRequest() {
        return new ReportJobRequest()
                .setJobId(JOB_ID);
    }

    private ReportJobResponse getReportJobResponse(ReportJobStatusEnum status) {
        return new ReportJobResponse(ID,
                new ReportShortResponse(ID, 1L, "Report"),
                new UserShortResponse(ID, USERNAME, "Domain"),
                status,
                ReportJobStateEnum.NORMAL,
                "msg",
                100L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Collections.emptyList(),
                true,
                0L,
                "comment",
                true,
                0,
                Collections.emptyList());
    }

    private ReportJobAddRequest getReportJobAddRequest() {
        final var parameters = new LinkedList<ReportJobFilterRequest>();
        parameters.add(
                new ReportJobFilterRequest()
                        .setFilterId(FILTER_ID)
                        .setOperationType(FilterOperationTypeEnum.IS_EQUAL)
                        .setParameters(Collections.singletonList(new Tuple().setValues(Collections.singletonList(new TupleValue().setValue("123"))))));
        return new ReportJobAddRequest()
                .setReportId(REPORT_ID)
                .setParameters(parameters);
    }

    private ReportPageResponse getReportPageResponse() {
        return new ReportPageResponse(1L, JOB_ID, 1L, 100L, Collections.emptyList());

    }

    private ReportPageRequest getReportPageRequest() {
        return new ReportPageRequest()
                .setJobId(JOB_ID)
                .setPageNumber(1L)
                .setRowsPerPage(100L);
    }

    private ReportResponse getReportResponse() {
        return new ReportResponse()
                .setId(ID)
                .setFilterGroup(
                        new FilterGroupResponse(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                true,
                                null,
                                null,
                                List.of(new FilterReportResponse(
                                        10L,
                                        null,
                                        FilterTypeEnum.VALUE_LIST,
                                        null,
                                        true,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                       null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
                                ),
                                        new FilterReportResponse(
                                                3L,
                                                null,
                                                FilterTypeEnum.CURRENT_LOGIN,
                                                null,
                                                false,
                                                null,
                                                null,
                                                null,
                                                null,
                                                null,
                                                Collections.singletonList(new FilterReportFieldResponse(
                                                        ID,
                                                        null,
                                                        null,
                                                        FilterFieldTypeEnum.CODE_FIELD,
                                                        null,
                                                        null,
                                                        null,
                                                        null,
                                                        null,
                                                        null,
                                                        null,
                                                        null,
                                                        null,
                                                        null,
                                                        null
                                                )),
                                                null,
                                                null,
                                                null,
                                                null,
                                                null,
                                                null
                                        )),
                                null,
                                null
                        ));
    }

    private ReportJobMetadataResponse getReportJobMetadataResponse() {
        return new ReportJobMetadataResponse(
                ID,
                TOTAL_ROW,
                Collections.emptyList()
        );
    }

    private ReportJobShareRequest getReportJobShareRequest() {
        return new ReportJobShareRequest()
                .setJobId(ID)
                .setUsers(Collections.singletonList(new UserRequest(USERNAME, "")));

    }

    private FilterReportResponse getFilterReportResponse(boolean mandatory) {
        return new FilterReportResponse(FILTER_ID,
                1L,
                FilterTypeEnum.VALUE_LIST,
                false,
                mandatory,
                true,
                "name",
                "code",
                "desc",
                1,
                Collections.emptyList(),
                "user",
                LocalDateTime.now(),
                LocalDateTime.now(),
                true,
                0L,
                Collections.emptyList());
    }

    private ReportJobHistoryRequestFilter getReportJobHistoryRequestFilter(List<Long> reports, List<Long> users, List<ReportJobStatusEnum> statuses, LocalDateTime to, LocalDateTime from){
        return new ReportJobHistoryRequestFilter()
                .setReportIds(reports)
                .setUsers(users)
                .setStatuses(statuses)
                .setFrom(from)
                .setTo(to);
    }
}
