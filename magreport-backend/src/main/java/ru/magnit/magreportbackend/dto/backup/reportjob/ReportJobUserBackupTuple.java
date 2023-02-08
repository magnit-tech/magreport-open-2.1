package ru.magnit.magreportbackend.dto.backup.reportjob;

import java.time.LocalDateTime;

public record ReportJobUserBackupTuple(

        Long reportJobId,
        Long userId,
        Long reportJobUserId,
        Long reportJobUserTypeId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
