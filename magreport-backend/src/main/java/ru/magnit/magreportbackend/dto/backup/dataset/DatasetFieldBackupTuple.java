package ru.magnit.magreportbackend.dto.backup.dataset;

import java.time.LocalDateTime;

public record DatasetFieldBackupTuple (
    Long datasetId,
    Long datasetFieldId,
    Long dataTypeId,
    Boolean isSync,
    String name,
    String description,
    LocalDateTime created,
    LocalDateTime modified
){}
