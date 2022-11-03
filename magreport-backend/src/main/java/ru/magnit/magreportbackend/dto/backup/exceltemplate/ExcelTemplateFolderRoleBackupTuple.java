package ru.magnit.magreportbackend.dto.backup.exceltemplate;

import java.time.LocalDateTime;

public record ExcelTemplateFolderRoleBackupTuple(

        Long excelTemplateFolderRoleId,
        Long excelTemplateFolderId,
        Long roleId,
        LocalDateTime created,
        LocalDateTime modified
) {
}
