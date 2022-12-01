package ru.magnit.magreportbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportFieldData;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportJobData;
import ru.magnit.magreportbackend.dto.request.olap.CubeField;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeOutRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequestNew;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldItemsOutRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldItemsRequest;
import ru.magnit.magreportbackend.dto.response.ExtOlapCubeResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapCubeResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapFieldItemsResponse;
import ru.magnit.magreportbackend.exception.OlapExternalServiceException;
import ru.magnit.magreportbackend.service.domain.JobDomainService;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalOlapService {
    private final JobDomainService jobDomainService;
    private final RestTemplate restTemplate;
    private final ExternalOlapManager externalOlapManager;

    public OlapCubeResponse getCube(OlapCubeRequest request) {
        jobDomainService.checkAccessForJob(request.getJobId());

        jobDomainService.updateJobStats(request.getJobId(), false, true, false);

        var jobData = jobDomainService.getJobData(request.getJobId());
        var outRequest = new OlapCubeOutRequest()
            .setJobId(request.getJobId())
            .setCubeSize(jobData.rowCount())
            .setColumnFields(request.getColumnFields())
            .setRowFields(request.getRowFields())
            .setRowsInterval(request.getRowsInterval())
            .setColumnsInterval(request.getColumnsInterval())
            .setFilterGroup(request.getFilterGroup())
            .setMetricFilterGroup(request.getMetricFilterGroup())
            .setMetrics(request.getMetrics())
            .setMetricPlacement(request.getMetricPlacement())
            .setFields(updateRequest(jobData))
            .setRowSort(request.getRowSort())
            .setColumnSort(request.getColumnSort());

        final var olapCubeResponse =
            restTemplate.postForObject(externalOlapManager.getExternalServiceCubeURL(request.getJobId()), outRequest, ExtOlapCubeResponse.class);
        externalOlapManager.onRequestComplete(request.getJobId());

        if (olapCubeResponse == null) {
            throw new OlapExternalServiceException("External OLAP Service not responded");
        }
        if (Boolean.FALSE.equals(olapCubeResponse.getSuccess())) {
            throw new OlapExternalServiceException(olapCubeResponse.getMessage());
        }
        return olapCubeResponse.getData();
    }

    public OlapFieldItemsResponse getFieldValues(OlapFieldItemsRequest request) {
        var jobData = jobDomainService.getJobData(request.getJobId());
        var outRequest = new OlapFieldItemsOutRequest();

        outRequest.setJobId(request.getJobId());
        outRequest.setCubeSize(jobData.rowCount());
        outRequest.setFilterGroup(request.getFilterGroup());
        outRequest.setFrom(request.getFrom());
        outRequest.setCount(request.getCount());
        outRequest.setFieldId(request.getFieldId());
        outRequest.setFields(updateRequest(jobData));

        final var result = restTemplate.postForObject(externalOlapManager.getExternalServiceFieldsURL(request.getJobId()), outRequest, OlapFieldItemsResponse.class);
        externalOlapManager.onRequestComplete(request.getJobId());

        return result;
    }

    private List<CubeField> updateRequest(ReportJobData jobData) {

        return jobData.reportData().fields()
            .stream()
            .filter(ReportFieldData::visible)
            .sorted(Comparator.comparingInt(ReportFieldData::ordinal))
            .map(f -> new CubeField(f.id(), f.dataType().name()))
            .toList();
    }

    public OlapCubeResponse getCubeNew(OlapCubeRequestNew request) {
        return null;
    }
}
