package ru.magnit.magreportbackend.mapper.reportjob;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobTupleField;
import ru.magnit.magreportbackend.dto.backup.reportjob.ReportJobTupleFieldBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportJobTupleFieldBackupMapper implements Mapper<ReportJobTupleFieldBackupTuple, ReportJobTupleField> {
    @Override
    public ReportJobTupleFieldBackupTuple from(ReportJobTupleField source) {
        return new ReportJobTupleFieldBackupTuple(
                source.getId(),
                source.getReportJobTuple().getId(),
                source.getFilterReportField().getId(),
                source.getValue(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
