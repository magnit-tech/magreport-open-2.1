package ru.magnit.magreportbackend.dto.backup.filterreport;

import java.time.LocalDateTime;

public record FilterReportGroupBackupTuple(

        Long filterReportGroupId,
        Long parentId,
        Long reportId,
        Long reportFilterGroupOperationTypeId,
        Long ordinal,
        boolean linkedFilters,
        boolean mandatory,
        String code,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
