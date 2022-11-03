package ru.magnit.magreportbackend.mapper.exceltemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.excel.ExcelTemplate;
import ru.magnit.magreportbackend.dto.backup.exceltemplate.ExcelTemplateBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ExcelTemplateBackupMapper implements Mapper<ExcelTemplateBackupTuple, ExcelTemplate> {
    @Override
    public ExcelTemplateBackupTuple from(ExcelTemplate source) {
        return new ExcelTemplateBackupTuple(
                source.getId(),
                source.getFolder().getId(),
                source.getUser().getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
