package ru.magnit.magreportbackend.dto.backup.reportjob;

import java.time.LocalDateTime;

public record ReportJobTupleBackupTuple(

        Long reportJobTupleId,
        Long reportJobFilterId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
