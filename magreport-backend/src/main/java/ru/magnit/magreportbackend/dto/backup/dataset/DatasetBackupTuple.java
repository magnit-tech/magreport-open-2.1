package ru.magnit.magreportbackend.dto.backup.dataset;

import java.time.LocalDateTime;


public record DatasetBackupTuple(
        Long datasetId,
        Long datasourceId,
        Long datasetTypeId,
        Long datasetFolderId,
        String catalogName,
        String schemaName,
        String objectName,
        String name,
        String description,
        Long userId,
        LocalDateTime created,
        LocalDateTime modified

) {
}

