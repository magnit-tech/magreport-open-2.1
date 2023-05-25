package ru.magnit.magreportbackend.dto.backup.filterreport;

import java.time.LocalDateTime;

public record FilterReportBackupTuple(

        Long filterReportId,
        Long filterReportGroupId,
        Long filterInstanceId,
        Long ordinal,
        Long userId,

        Long maxCountItems,
        boolean isHidden,
        boolean isMandatory,
        boolean isRootMandatory,
        String code,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
