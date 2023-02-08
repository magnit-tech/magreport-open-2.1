package ru.magnit.magreportbackend.mapper.reportjob;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.reportjobstats.ReportJobStatistics;
import ru.magnit.magreportbackend.dto.backup.reportjob.ReportJobStatisticsBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportJobStatisticsBackupMapper implements Mapper<ReportJobStatisticsBackupTuple, ReportJobStatistics> {
    @Override
    public ReportJobStatisticsBackupTuple from(ReportJobStatistics source) {
        return new ReportJobStatisticsBackupTuple(
                source.getId(),
                source.getReportJob().getId(),
                source.getReport().getId(),
                source.getUser().getId(),
                source.getStatus().getId(),
                source.getState().getId(),
                source.getRowCount(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime(),
                source.getExportExcelCount(),
                source.getOlapRequestCount(),
                source.getIsShare()
        );
    }
}
