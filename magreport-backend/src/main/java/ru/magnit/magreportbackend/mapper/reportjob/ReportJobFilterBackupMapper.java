package ru.magnit.magreportbackend.mapper.reportjob;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobFilter;
import ru.magnit.magreportbackend.dto.backup.reportjob.ReportJobFilterBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportJobFilterBackupMapper implements Mapper<ReportJobFilterBackupTuple, ReportJobFilter> {
    @Override
    public ReportJobFilterBackupTuple from(ReportJobFilter source) {
        return new ReportJobFilterBackupTuple(
                source.getId(),
                source.getReportJob() == null ? null : source.getReportJob().getId(),
                source.getFilterReport().getId(),
                source.getFilterOperationType().getId(),
                source.getScheduleTask() == null? null : source.getScheduleTask().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
