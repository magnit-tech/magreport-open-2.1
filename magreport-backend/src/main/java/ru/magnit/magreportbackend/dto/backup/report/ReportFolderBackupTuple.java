package ru.magnit.magreportbackend.dto.backup.report;

import java.time.LocalDateTime;

public record ReportFolderBackupTuple(

        Long reportFolderId,
        Long parentId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
