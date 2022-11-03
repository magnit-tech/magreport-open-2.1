package ru.magnit.magreportbackend.mapper.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.report.ReportField;
import ru.magnit.magreportbackend.dto.backup.report.ReportFieldBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportFieldBackupMapper implements Mapper<ReportFieldBackupTuple, ReportField> {
    @Override
    public ReportFieldBackupTuple from(ReportField source) {
        return new ReportFieldBackupTuple(
                source.getId(),
                source.getReport().getId(),
                source.getPivotFieldType().getId(),
                source.getDataSetField().getId(),
                source.getOrdinal(),
                source.getVisible(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
