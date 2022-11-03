package ru.magnit.magreportbackend.dto.backup.reportjob;

import java.time.LocalDateTime;

public record ReportJobStatisticsBackupTuple(

        Long reportJobStatistics,
        Long reportJobId,
        Long reportId,
        Long userId,
        Long reportJobStatusId,
        Long reportJobStateId,
        Long rowCount,
        LocalDateTime created,
        LocalDateTime modified

) {
}
