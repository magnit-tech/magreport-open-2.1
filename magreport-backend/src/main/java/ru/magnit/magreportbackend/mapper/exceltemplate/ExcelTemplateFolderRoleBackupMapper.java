package ru.magnit.magreportbackend.mapper.exceltemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.excel.ExcelTemplateFolderRole;
import ru.magnit.magreportbackend.dto.backup.exceltemplate.ExcelTemplateFolderRoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ExcelTemplateFolderRoleBackupMapper implements Mapper<ExcelTemplateFolderRoleBackupTuple, ExcelTemplateFolderRole> {
    @Override
    public ExcelTemplateFolderRoleBackupTuple from(ExcelTemplateFolderRole source) {
        return new ExcelTemplateFolderRoleBackupTuple(
                source.getId(),
                source.getFolder().getId(),
                source.getRole().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
