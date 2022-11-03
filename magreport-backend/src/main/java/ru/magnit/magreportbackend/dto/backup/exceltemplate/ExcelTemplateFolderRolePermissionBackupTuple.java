package ru.magnit.magreportbackend.dto.backup.exceltemplate;

import java.time.LocalDateTime;

public record ExcelTemplateFolderRolePermissionBackupTuple(

        Long excelTemplateFolderRolePermissionId,
        Long excelTemplateFolderRoleId,
        Long folderAuthorityId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
