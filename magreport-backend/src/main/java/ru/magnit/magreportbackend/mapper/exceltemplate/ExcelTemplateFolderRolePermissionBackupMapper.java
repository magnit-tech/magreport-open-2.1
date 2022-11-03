package ru.magnit.magreportbackend.mapper.exceltemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.excel.ExcelTemplateFolderRolePermission;
import ru.magnit.magreportbackend.dto.backup.exceltemplate.ExcelTemplateFolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ExcelTemplateFolderRolePermissionBackupMapper implements Mapper<ExcelTemplateFolderRolePermissionBackupTuple, ExcelTemplateFolderRolePermission> {
    @Override
    public ExcelTemplateFolderRolePermissionBackupTuple from(ExcelTemplateFolderRolePermission source) {
        return new ExcelTemplateFolderRolePermissionBackupTuple(
                source.getId(),
                source.getFolderRole().getId(),
                source.getAuthority().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
