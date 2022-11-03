package ru.magnit.magreportbackend.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.magnit.magreportbackend.domain.olap.OlapConfiguration;
import ru.magnit.magreportbackend.domain.olap.ReportOlapConfiguration;
import ru.magnit.magreportbackend.domain.report.Report;
import ru.magnit.magreportbackend.domain.reportjob.ReportJob;
import ru.magnit.magreportbackend.domain.user.User;
import ru.magnit.magreportbackend.dto.request.olap.OlapConfigUpdateRequest;
import ru.magnit.magreportbackend.dto.request.olap.ReportOlapConfigAddRequest;
import ru.magnit.magreportbackend.dto.request.olap.ReportOlapConfigSetShareRequest;
import ru.magnit.magreportbackend.dto.request.olap.UsersReceivedMyJobsRequest;
import ru.magnit.magreportbackend.dto.response.olap.OlapConfigResponse;
import ru.magnit.magreportbackend.dto.response.olap.ReportOlapConfigResponse;
import ru.magnit.magreportbackend.dto.response.user.UserShortInfoResponse;
import ru.magnit.magreportbackend.exception.InvalidParametersException;
import ru.magnit.magreportbackend.mapper.auth.UserShortInfoResponseMapper;
import ru.magnit.magreportbackend.mapper.olap.OlapConfigurationMapper;
import ru.magnit.magreportbackend.mapper.olap.OlapConfigurationResponseMapper;
import ru.magnit.magreportbackend.mapper.olap.ReportOlapConfigurationCloner;
import ru.magnit.magreportbackend.mapper.olap.ReportOlapConfigurationMapper;
import ru.magnit.magreportbackend.mapper.olap.ReportOlapConfigurationMerger;
import ru.magnit.magreportbackend.mapper.olap.ReportOlapConfigurationResponseMapper;
import ru.magnit.magreportbackend.repository.OlapConfigurationRepository;
import ru.magnit.magreportbackend.repository.ReportOlapConfigurationRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class OlapConfigurationDomainServiceTest {


    @Mock
    private OlapConfigurationRepository repository;
    @Mock
    private ReportOlapConfigurationRepository reportOlapConfigurationRepository;
    @Mock
    private OlapConfigurationMapper olapConfigurationMapper;
    @Mock
    private OlapConfigurationResponseMapper olapConfigurationResponseMapper;
    @Mock
    private ReportOlapConfigurationMapper reportOlapConfigurationMapper;
    @Mock
    private ReportOlapConfigurationResponseMapper reportOlapConfigurationResponseMapper;
    @Mock
    private ReportOlapConfigurationCloner reportOlapConfigurationCloner;
    @Mock
    private ReportOlapConfigurationMerger reportOlapConfigurationMerger;
    @Mock
    private UserShortInfoResponseMapper userShortInfoResponseMapper;

    @InjectMocks
    private OlapConfigurationDomainService domainService;

    private final static Long ID = 1L;
    private final static Long USER_ID = 2L;
    private final static Long REPORT_ID = 3L;
    private final static Long JOB_ID = 4L;
    private final static String NAME = "NAME";


    @Test
    void updateReportOlapConfigurationTest1() {

        when(reportOlapConfigurationMapper.from((ReportOlapConfigAddRequest) any())).thenReturn(getReportOlapConfigurationData());
        when(olapConfigurationMapper.from((OlapConfigUpdateRequest) any())).thenReturn(getOlapConfigurationData());
        when(repository.save(any())).thenReturn(getOlapConfigurationData());
        when(reportOlapConfigurationRepository.save(any())).thenReturn(getReportOlapConfigurationData());

        var result = domainService.updateReportOlapConfiguration(getReportOlapConfigAddRequest(true), USER_ID);

        assertEquals(ID, result);

        verify(reportOlapConfigurationMapper).from((ReportOlapConfigAddRequest) any());
        verify(olapConfigurationMapper).from((OlapConfigUpdateRequest) any());
        verify(repository).save(any());
        verify(reportOlapConfigurationRepository).save(any());
        verify(reportOlapConfigurationRepository).disableDefaultFlagForReportAndUser(anyLong(), any());
        verifyNoMoreInteractions(reportOlapConfigurationMapper, olapConfigurationMapper, repository, reportOlapConfigurationRepository);


    }

    @Test
    void updateReportOlapConfigurationTest2() {

        when(reportOlapConfigurationRepository.getReferenceById(anyLong())).thenReturn(getReportOlapConfigurationData());
        when(reportOlapConfigurationMerger.merge(any(), any())).thenReturn(getReportOlapConfigurationData());
        when(reportOlapConfigurationRepository.save(any())).thenReturn(getReportOlapConfigurationData());

        var result = domainService.updateReportOlapConfiguration(getReportOlapConfigAddRequest(false).setReportOlapConfigId(ID), USER_ID);

        assertEquals(ID, result);

        verify(reportOlapConfigurationRepository).getReferenceById(any());
        verify(reportOlapConfigurationMerger).merge(any(), any());
        verify(reportOlapConfigurationRepository).save(any());
        verifyNoMoreInteractions(reportOlapConfigurationRepository, reportOlapConfigurationMerger);

    }

    @Test
    void updateReportOlapConfigurationTest3() {

        when(reportOlapConfigurationRepository.getReferenceById(anyLong())).thenReturn(getReportOlapConfigurationData());
        var roc = getReportOlapConfigAddRequest(false).setReportOlapConfigId(ID);
        Assertions.assertThrows(InvalidParametersException.class, () -> domainService.updateReportOlapConfiguration(roc, ID));

        verify(reportOlapConfigurationRepository).getReferenceById(any());
        verifyNoMoreInteractions(reportOlapConfigurationRepository);
    }


    @Test
    void getOlapConfiguration() {

        when(olapConfigurationResponseMapper.from((OlapConfiguration) any())).thenReturn(getOlapConfigResponse());
        when(repository.getReferenceById(any())).thenReturn(getOlapConfigurationData());

        var result = domainService.getOlapConfiguration(ID);

        assertNotNull(result);

        verify(olapConfigurationResponseMapper).from((OlapConfiguration) any());
        verify(repository).getReferenceById(any());
        verifyNoMoreInteractions(olapConfigurationResponseMapper,repository);

    }

    @Test
    void getReportOlapConfiguration() {

        when(reportOlapConfigurationResponseMapper.from((ReportOlapConfiguration) any())).thenReturn(getReportOlapConfigResponse());
        when(reportOlapConfigurationRepository.getReferenceById(any())).thenReturn(getReportOlapConfigurationData());

        var result = domainService.getReportOlapConfiguration(ID);

        assertNotNull(result);

        verify(reportOlapConfigurationResponseMapper).from((ReportOlapConfiguration) any());
        verify(reportOlapConfigurationRepository).getReferenceById(any());
        verifyNoMoreInteractions(reportOlapConfigurationResponseMapper,reportOlapConfigurationRepository);

    }

    @Test
    void getListUsersReceivedAuthorJob() {

        when(reportOlapConfigurationRepository.getReportOlapConfigurationByCreatorIdAndCreatedDateTimeAfter(anyLong(),any())).thenReturn(Collections.singletonList(getReportOlapConfigurationData()));
        when(userShortInfoResponseMapper.from((User) any())).thenReturn(getUserShortInfoResponse());

        var result = domainService.getListUsersReceivedAuthorJob(new UsersReceivedMyJobsRequest().setLastDays(1L).setTopN(1L), USER_ID);

        assertEquals(1,result.size());

        verify(reportOlapConfigurationRepository).getReportOlapConfigurationByCreatorIdAndCreatedDateTimeAfter(anyLong(),any());
        verify(userShortInfoResponseMapper).from((User) any());
        verifyNoMoreInteractions(reportOlapConfigurationRepository,userShortInfoResponseMapper);

    }

    @Test
    void setDefaultReportConfigurationTest1() {

        when(reportOlapConfigurationRepository.getReferenceById(any())).thenReturn(getReportOlapConfigurationData());

        domainService.setDefaultReportConfiguration(anyLong());

        verify(reportOlapConfigurationRepository).getReferenceById(any());
        verify(reportOlapConfigurationRepository).save(any());
        verify(reportOlapConfigurationRepository).disableDefaultFlagForReport(anyLong());
        verifyNoMoreInteractions(reportOlapConfigurationRepository);
    }

    @Test
    void setDefaultReportConfigurationTest2() {

        when(reportOlapConfigurationRepository.getReferenceById(any())).thenReturn(getReportOlapConfigurationData().setReportJob(new ReportJob(JOB_ID)));

        Assertions.assertThrows(InvalidParametersException.class, () -> domainService.setDefaultReportConfiguration(ID));

        verify(reportOlapConfigurationRepository).getReferenceById(any());
        verifyNoMoreInteractions(reportOlapConfigurationRepository);
    }

    @Test
    void deleteOlapConfigurationTest1() {
        when(reportOlapConfigurationRepository.getReportOlapConfigurationsByOlapConfigurationId(any())).thenReturn(Collections.singletonList(getReportOlapConfigurationData()));
        when(repository.getReferenceById(any())).thenReturn(getOlapConfigurationData());

        domainService.deleteOlapConfiguration(ID, USER_ID);

        verify(repository).getReferenceById(any());
        verify(repository).deleteById(any());
        verify(reportOlapConfigurationRepository).deleteAll(anyCollection());
        verifyNoMoreInteractions(repository,reportOlapConfigurationRepository);
    }
    @Test
    void deleteOlapConfigurationTest2() {

        when(repository.getReferenceById(any())).thenReturn(getOlapConfigurationData());

        Assertions.assertThrows(InvalidParametersException.class, () -> domainService.deleteOlapConfiguration(ID, ID));

        verify(repository).getReferenceById(any());
        verifyNoMoreInteractions(repository,reportOlapConfigurationRepository);
    }
    @Test
    void deleteReportOlapConfigurationTest1() {

        when(reportOlapConfigurationRepository.getReferenceById(any())).thenReturn(getReportOlapConfigurationData());

        domainService.deleteReportOlapConfiguration(ID, USER_ID);

        verify(reportOlapConfigurationRepository).getReferenceById(any());
        verify(reportOlapConfigurationRepository).deleteById(any());
        verifyNoMoreInteractions(reportOlapConfigurationRepository);
    }

    @Test
    void deleteReportOlapConfigurationTest2() {

        when(reportOlapConfigurationRepository.getReferenceById(any())).thenReturn(getReportOlapConfigurationData());

        Assertions.assertThrows(InvalidParametersException.class, () -> domainService.deleteReportOlapConfiguration(ID, ID));

        verify(reportOlapConfigurationRepository).getReferenceById(any());
        verifyNoMoreInteractions(reportOlapConfigurationRepository);
    }

    @Test
    void updateSharedStatusOlapReportConfigTest1() {

        when(reportOlapConfigurationRepository.getReferenceById(any())).thenReturn(getReportOlapConfigurationData().setId(ID));

        domainService.updateSharedStatusOlapReportConfig(getReportOlapConfigSetShareRequest(),USER_ID);

        verify(reportOlapConfigurationRepository).getReferenceById(any());
        verify(reportOlapConfigurationRepository).save(any());
        verifyNoMoreInteractions(reportOlapConfigurationRepository);
    }

    @Test
    void updateSharedStatusOlapReportConfigTest2() {

        when(reportOlapConfigurationRepository.getReferenceById(any())).thenReturn(getReportOlapConfigurationData().setId(ID));

        var request = getReportOlapConfigSetShareRequest();
        Assertions.assertThrows(InvalidParametersException.class, () -> domainService.updateSharedStatusOlapReportConfig(request,ID));

        verify(reportOlapConfigurationRepository).getReferenceById(any());
        verifyNoMoreInteractions(reportOlapConfigurationRepository);
    }

    @Test
    void getAvailableReportOlapConfigurationForJob() {

        when(reportOlapConfigurationRepository.getROCByReport(anyLong())).thenReturn(Collections.singletonList(getReportOlapConfigurationData()));
        when(reportOlapConfigurationRepository.getROCByUserAndJob(any(), anyLong())).thenReturn(Collections.singletonList(getReportOlapConfigurationData()));
        when(reportOlapConfigurationRepository.getROCByReportAndUser(anyLong(),any())).thenReturn(Collections.singletonList(getReportOlapConfigurationData()));
        when(reportOlapConfigurationRepository.getSharedROCByJob(anyLong())).thenReturn(Collections.singletonList(getReportOlapConfigurationData()));
        when(reportOlapConfigurationResponseMapper.from(anyList())).thenReturn(Collections.singletonList(getReportOlapConfigResponse()));

        var result = domainService.getAvailableReportOlapConfigurationForJob(JOB_ID, REPORT_ID,USER_ID);

        assertEquals(1, result.getMyReportConfigs().size());
        assertEquals(1, result.getMyJobConfig().size());
        assertEquals(1, result.getCommonReportConfigs().size());
        assertEquals(1, result.getSharedJobConfig().size());

        verify(reportOlapConfigurationRepository).getROCByReport(anyLong());
        verify(reportOlapConfigurationRepository).getROCByUserAndJob(any(),anyLong());
        verify(reportOlapConfigurationRepository).getROCByReportAndUser(anyLong(),any());
        verify(reportOlapConfigurationRepository).getSharedROCByJob(anyLong());
        verify(reportOlapConfigurationResponseMapper, times(4)).from(anyList());
        verifyNoMoreInteractions(reportOlapConfigurationRepository, reportOlapConfigurationResponseMapper);
    }

    @Test
    void getCurrentConfigurationTest1() {

        when(reportOlapConfigurationRepository.findCurrentReportOlapConfiguration(anyLong(),anyLong())).thenReturn(Optional.of(getReportOlapConfigurationData()));

        var result = domainService.getCurrentConfiguration(JOB_ID, REPORT_ID, USER_ID);

        assertEquals(ID, result);

        verify(reportOlapConfigurationRepository).findCurrentReportOlapConfiguration(anyLong(),anyLong());
        verifyNoMoreInteractions(reportOlapConfigurationRepository);
    }

    @Test
    void getCurrentConfigurationTest2() {
        when(reportOlapConfigurationRepository.findCurrentReportOlapConfiguration(anyLong(),anyLong())).thenReturn(Optional.empty());
        when(reportOlapConfigurationRepository.save(any())).thenReturn(getReportOlapConfigurationData());

        var result = domainService.getCurrentConfiguration(JOB_ID, REPORT_ID, USER_ID);

        assertEquals(ID, result);

        verify(reportOlapConfigurationRepository).findCurrentReportOlapConfiguration(anyLong(),anyLong());
        verify(reportOlapConfigurationRepository).getROCByReport(anyLong());
        verify(reportOlapConfigurationRepository).getROCByReportAndUser(anyLong(),any());
        verify(reportOlapConfigurationRepository).save(any());
        verifyNoMoreInteractions(reportOlapConfigurationRepository);
    }

    @Test
    void createCurrentConfigurationForUsers() {

        when(reportOlapConfigurationRepository.findCurrentReportOlapConfiguration(anyLong(),anyLong())).thenReturn(Optional.of(getReportOlapConfigurationData()));
        when(reportOlapConfigurationCloner.clone((ReportOlapConfiguration) any())).thenReturn(getReportOlapConfigurationData());


        domainService.createCurrentConfigurationForUsers(Collections.singletonList(USER_ID), JOB_ID, ID);

        verify(reportOlapConfigurationRepository).findCurrentReportOlapConfiguration(anyLong(),anyLong());
        verify(reportOlapConfigurationRepository).save(any());
        verify(reportOlapConfigurationRepository).clearCurrentConfigForSharedUsers(anyLong(),anyLong());
        verifyNoMoreInteractions(reportOlapConfigurationRepository);

    }


    private ReportOlapConfigAddRequest getReportOlapConfigAddRequest(boolean flag) {
        return new ReportOlapConfigAddRequest()
                .setReportId(REPORT_ID)
                .setUserId(USER_ID)
                .setIsDefault(flag)
                .setIsCurrent(flag)
                .setIsShare(flag);
    }

    private ReportOlapConfiguration getReportOlapConfigurationData() {
        return new ReportOlapConfiguration()
                .setId(ID)
                .setOlapConfiguration(getOlapConfigurationData())
                .setReport(new Report().setId(REPORT_ID))
                .setCreator(new User(USER_ID));
    }

    private ReportOlapConfigResponse getReportOlapConfigResponse(){
        return new ReportOlapConfigResponse();
    }

    private OlapConfiguration getOlapConfigurationData() {
        return new OlapConfiguration()
                .setUser(new User().setId(USER_ID));
    }

    private OlapConfigResponse getOlapConfigResponse(){
        return new OlapConfigResponse();
    }

    private UserShortInfoResponse getUserShortInfoResponse(){
        return new UserShortInfoResponse()
                .setLogin(NAME);
    }

    private ReportOlapConfigSetShareRequest getReportOlapConfigSetShareRequest(){
        return new ReportOlapConfigSetShareRequest()
                .setReportOlapConfigId(ID)
                .setShare(true);
    }

}
