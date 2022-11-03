package ru.magnit.magreportbackend.dto.backup.exceltemplate;

import java.time.LocalDateTime;

public record ExcelTemplateFolderBackupTuple(
        Long excelTemplateFolderId,
        Long parentId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
