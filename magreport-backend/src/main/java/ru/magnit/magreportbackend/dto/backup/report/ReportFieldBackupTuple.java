package ru.magnit.magreportbackend.dto.backup.report;

import java.time.LocalDateTime;

public record ReportFieldBackupTuple(

        Long reportFieldId,
        Long reportId,
        Long pivotFieldTypeId,
        Long datasetFieldId,
        int ordinal,
        boolean visible,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
