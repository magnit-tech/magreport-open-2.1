package ru.magnit.magreportbackend.mapper.reportjob;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobTuple;
import ru.magnit.magreportbackend.dto.backup.reportjob.ReportJobTupleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportJobTupleBackupMapper implements Mapper<ReportJobTupleBackupTuple, ReportJobTuple> {
    @Override
    public ReportJobTupleBackupTuple from(ReportJobTuple source) {
        return new ReportJobTupleBackupTuple(
                source.getId(),
                source.getReportJobFilter().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
