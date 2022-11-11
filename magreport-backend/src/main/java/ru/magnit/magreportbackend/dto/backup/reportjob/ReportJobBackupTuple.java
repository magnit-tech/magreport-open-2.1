package ru.magnit.magreportbackend.dto.backup.reportjob;

import java.time.LocalDateTime;

public record ReportJobBackupTuple(

        Long reportJobId,
        Long reportId,
        Long userId,
        Long reportJobStatusId,
        Long reportJobStateId,
        Long rowCount,
        String message,
        String sqlQuery,
        LocalDateTime created,
        LocalDateTime modified,
        String comment
) {
}
