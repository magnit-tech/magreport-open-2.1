package ru.magnit.magreportbackend.mapper.reportjob;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.reportjobstats.ReportJobStatistics;
import ru.magnit.magreportbackend.dto.response.reportjob.ReportJobStatisticsResponse;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportJobStatisticsResponseMapper implements Mapper<ReportJobStatisticsResponse, ReportJobStatistics> {

    @Override
    public ReportJobStatisticsResponse from(ReportJobStatistics source) {
        return new ReportJobStatisticsResponse(
            source.getStatus().getName() + " (" + source.getStatus().getDescription() + ")",
            source.getState().getName() + " (" + source.getState().getDescription() + ")",
            source.getRowCount(),
            source.getExportExcelCount(),
            source.getIsShare(),
            source.getOlapRequestCount(),
            source.getCreatedDateTime()
        );
    }
}
