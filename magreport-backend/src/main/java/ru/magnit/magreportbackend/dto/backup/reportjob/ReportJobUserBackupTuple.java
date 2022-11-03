package ru.magnit.magreportbackend.dto.backup.reportjob;

public record ReportJobUserBackupTuple(

        Long reportJobId,
        Long userId,
        Long reportJobUserId,
        Long reportJobUserTypeId

) {
}
