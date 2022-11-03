package ru.magnit.magreportbackend.dto.backup.report;

import java.time.LocalDateTime;

public record ReportExcelTemplateBackupTuple(

        Long reportExcelTemplateId,
        Long excelTemplateId,
        Long reportId,
        boolean isDefault,
        LocalDateTime created,
        LocalDateTime modified

) {
}
