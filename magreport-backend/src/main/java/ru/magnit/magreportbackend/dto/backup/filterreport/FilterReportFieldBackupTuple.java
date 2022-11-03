package ru.magnit.magreportbackend.dto.backup.filterreport;

import java.time.LocalDateTime;

public record FilterReportFieldBackupTuple(

        Long filterReportFieldId,
        Long filterReportId,
        Long filterInstanceFieldId,
        Long reportFieldId,
        Long ordinal,
        boolean expand,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
