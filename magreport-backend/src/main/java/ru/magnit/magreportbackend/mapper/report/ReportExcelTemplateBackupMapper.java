package ru.magnit.magreportbackend.mapper.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.excel.ReportExcelTemplate;
import ru.magnit.magreportbackend.dto.backup.report.ReportExcelTemplateBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportExcelTemplateBackupMapper implements Mapper<ReportExcelTemplateBackupTuple, ReportExcelTemplate> {
    @Override
    public ReportExcelTemplateBackupTuple from(ReportExcelTemplate source) {
        return new ReportExcelTemplateBackupTuple(
                source.getId(),
                source.getExcelTemplate().getId(),
                source.getReport().getId(),
                source.getIsDefault(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
