package ru.magnit.magreportbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.request.olap.OlapConfigRequest;
import ru.magnit.magreportbackend.dto.request.olap.ReportOlapConfigAddRequest;
import ru.magnit.magreportbackend.dto.request.olap.ReportOlapConfigRequest;
import ru.magnit.magreportbackend.dto.request.olap.ReportOlapConfigSetShareRequest;
import ru.magnit.magreportbackend.dto.request.olap.UsersReceivedMyJobsRequest;
import ru.magnit.magreportbackend.dto.request.reportjob.ReportJobRequest;
import ru.magnit.magreportbackend.dto.response.olap.OlapAvailableConfigurationsResponse;
import ru.magnit.magreportbackend.dto.response.olap.ReportOlapConfigResponse;
import ru.magnit.magreportbackend.dto.response.user.UserShortInfoResponse;
import ru.magnit.magreportbackend.service.domain.JobDomainService;
import ru.magnit.magreportbackend.service.domain.OlapConfigurationDomainService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OlapConfigurationService {

    private final OlapConfigurationDomainService olapConfigurationDomainService;
    private final UserDomainService userDomainService;
    private final JobDomainService jobDomainService;

    public ReportOlapConfigResponse addOlapReportConfig(ReportOlapConfigAddRequest request) {
        var currentUser = userDomainService.getCurrentUser();
        var id = olapConfigurationDomainService.updateReportOlapConfiguration(request, currentUser.getId());
        return olapConfigurationDomainService.getReportOlapConfiguration(id);
    }
    public ReportOlapConfigResponse getOlapReportConfig(ReportOlapConfigRequest request) {
        return olapConfigurationDomainService.getReportOlapConfiguration(request.getReportOlapConfigId());
    }
    public void deleteOlapConfig(OlapConfigRequest request) {
        var currentUser = userDomainService.getCurrentUser();
        olapConfigurationDomainService.deleteOlapConfiguration(request.getOlapConfigId(), currentUser.getId());
    }
    public void deleteOlapReportConfig(ReportOlapConfigRequest request) {
        var currentUser = userDomainService.getCurrentUser();
        olapConfigurationDomainService.deleteReportOlapConfiguration(request.getReportOlapConfigId(), currentUser.getId());
    }
    public List<UserShortInfoResponse> getUsersReceivedMyJobs(UsersReceivedMyJobsRequest request) {
        var currentUser = userDomainService.getCurrentUser();
        return olapConfigurationDomainService.getListUsersReceivedAuthorJob(request, currentUser.getId());
    }
    public void setDefaultReportConfiguration(ReportOlapConfigRequest request) {
        olapConfigurationDomainService.setDefaultReportConfiguration(request.getReportOlapConfigId());
    }
    public void setSharedStatusReportConfiguration(ReportOlapConfigSetShareRequest request) {
        var currentUser = userDomainService.getCurrentUser();
        olapConfigurationDomainService.updateSharedStatusOlapReportConfig(request, currentUser.getId());
    }
    public OlapAvailableConfigurationsResponse getAvailableConfigurationsForJob(ReportJobRequest request) {
        var job = jobDomainService.getJob(request.getJobId());
        var currentUser = userDomainService.getCurrentUser();
        return olapConfigurationDomainService.getAvailableReportOlapConfigurationForJob(job.getId(), job.getReport().id(), currentUser.getId());
    }
    public ReportOlapConfigResponse getCurrentConfiguration(ReportJobRequest request) {
        var job = jobDomainService.getJob(request.getJobId());
        var currentUser = userDomainService.getCurrentUser();

        var currentConfigId = olapConfigurationDomainService.getCurrentConfiguration(job.getId(), job.getReport().id(), currentUser.getId());

        return olapConfigurationDomainService.getReportOlapConfiguration(currentConfigId);
    }
}
