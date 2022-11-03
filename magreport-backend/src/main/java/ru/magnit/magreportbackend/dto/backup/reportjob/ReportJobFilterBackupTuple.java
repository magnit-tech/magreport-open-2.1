package ru.magnit.magreportbackend.dto.backup.reportjob;

import java.time.LocalDateTime;

public record ReportJobFilterBackupTuple(

        Long reportJobFilterId,
        Long reportJobId,
        Long filterReportId,
        Long filterOperationTypeId,
        Long scheduleTaskId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
