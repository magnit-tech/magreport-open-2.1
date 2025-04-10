package ru.magnit.magreportbackend.dto.backup.dataset;

import java.time.LocalDateTime;

public record DataSetFieldBackupTuple(
        Long datasetFieldId,
        Long datasetId,
        Long dataTypeId,
        Boolean isSync,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
