package ru.magnit.magreportbackend.mapper.exceltemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.excel.ExcelTemplateFolder;
import ru.magnit.magreportbackend.dto.backup.exceltemplate.ExcelTemplateFolderBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ExcelTemplateFolderBackupMapper implements Mapper<ExcelTemplateFolderBackupTuple, ExcelTemplateFolder> {
    @Override
    public ExcelTemplateFolderBackupTuple from(ExcelTemplateFolder source) {
        return new ExcelTemplateFolderBackupTuple(
                source.getId(),
                source.getParentFolder() == null ? null : source.getParentFolder().getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
