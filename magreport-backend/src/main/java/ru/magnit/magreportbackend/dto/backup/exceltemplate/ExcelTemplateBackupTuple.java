package ru.magnit.magreportbackend.dto.backup.exceltemplate;

import java.time.LocalDateTime;

public record ExcelTemplateBackupTuple(

        Long excelTemplateId,
        Long excelTemplateFolderId,
        Long userId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
