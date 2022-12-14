package ru.magnit.magreportbackend.mapper.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.report.Report;
import ru.magnit.magreportbackend.dto.response.reportjob.ScheduledReportResponse;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ScheduledReportResponseMapper implements Mapper<ScheduledReportResponse, Report> {
    @Override
    public ScheduledReportResponse from(Report source) {
        return new ScheduledReportResponse(source.getId(), source.getName());
    }
}
