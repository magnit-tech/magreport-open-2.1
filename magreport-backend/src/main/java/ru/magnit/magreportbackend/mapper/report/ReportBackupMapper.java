package ru.magnit.magreportbackend.mapper.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.report.Report;
import ru.magnit.magreportbackend.dto.backup.report.ReportBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportBackupMapper implements Mapper<ReportBackupTuple, Report> {
    @Override
    public ReportBackupTuple from(Report source) {
        return new ReportBackupTuple(
                source.getId(),
                source.getFolder().getId(),
                source.getDataSet().getId(),
                source.getRequirementsLink(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
