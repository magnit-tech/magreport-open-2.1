package ru.magnit.magreportbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequestV2;
import ru.magnit.magreportbackend.dto.response.olap.OlapCubeResponseV2;
import ru.magnit.magreportbackend.service.domain.JobDomainService;
import ru.magnit.magreportbackend.service.domain.OlapDomainService;
import ru.magnit.magreportbackend.service.domain.OlapUserChoiceDomainService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("Duplicates")
public class OlapServiceV2 {
    @Value("${magreport.olap.max-data-volume}")
    private long maxDataVolume = 1000;

    private final JobDomainService jobDomainService;
    private final UserDomainService userDomainService;
    private final OlapUserChoiceDomainService olapUserChoiceDomainService;
    private final OlapDomainService olapDomainService;
    private final DerivedFieldService derivedFieldService;

    public OlapCubeResponseV2 getCube(OlapCubeRequestV2 request) {
        var currentUser = userDomainService.getCurrentUser();
        jobDomainService.checkAccessForJob(request.getJobId());

        jobDomainService.updateJobStats(request.getJobId(), false, true, false);

        log.debug("Start processing cube");
        var startTime = System.currentTimeMillis();
        final var jobData = jobDomainService.getJobData(request.getJobId());
        var endTime = System.currentTimeMillis() - startTime;
        log.debug("Job data acquired: " + endTime);

        olapUserChoiceDomainService.setOlapUserChoice(jobData.reportId(), currentUser.getId(), true);

        startTime = System.currentTimeMillis();
        var sourceCube = olapDomainService.getCubeData(jobData);
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Report data acquired: " + endTime);

        OlapCubeRequestV2 cubeRequest;
        if (request.hasDerivedFields()) {
            startTime = System.currentTimeMillis();
            final var result = derivedFieldService.preProcessCubeV2(sourceCube, request);
            sourceCube = result.getL();
            cubeRequest = result.getR();
            endTime = System.currentTimeMillis() - startTime;
            log.debug("Derived fields calculated: " + endTime);
        } else {
            cubeRequest = request;
        }

        return  new OlapCubeResponseV2();
    }
}
