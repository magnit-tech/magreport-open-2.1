package ru.magnit.magreportbackend.dto.backup.reportjob;

import java.time.LocalDateTime;

public record ReportJobTupleFieldBackupTuple(

        Long reportJobTupleFieldId,
        Long reportJobTupleId,
        Long filterReportFieldId,
        String val,
        LocalDateTime created,
        LocalDateTime modified

) {
}
