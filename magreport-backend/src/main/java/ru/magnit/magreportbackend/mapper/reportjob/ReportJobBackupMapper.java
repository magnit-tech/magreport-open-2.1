package ru.magnit.magreportbackend.mapper.reportjob;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.reportjob.ReportJob;
import ru.magnit.magreportbackend.dto.backup.reportjob.ReportJobBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportJobBackupMapper implements Mapper<ReportJobBackupTuple, ReportJob> {
    @Override
    public ReportJobBackupTuple from(ReportJob source) {
        return new ReportJobBackupTuple(
                source.getId(),
                source.getReport().getId(),
                source.getUser().getId(),
                source.getStatus().getId(),
                source.getState().getId(),
                source.getRowCount(),
                source.getMessage(),
                source.getSqlQuery(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime(),
                source.getComment()
        );
    }
}
