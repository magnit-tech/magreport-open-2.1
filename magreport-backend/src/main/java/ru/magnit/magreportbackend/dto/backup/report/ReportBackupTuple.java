package ru.magnit.magreportbackend.dto.backup.report;

import java.time.LocalDateTime;

public record ReportBackupTuple(

        Long reportId,
        Long reportFolderId,
        Long datasetId,
        String requirementsURL,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
