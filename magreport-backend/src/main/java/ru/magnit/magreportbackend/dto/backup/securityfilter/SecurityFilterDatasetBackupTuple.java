package ru.magnit.magreportbackend.dto.backup.securityfilter;

import java.time.LocalDateTime;

public record SecurityFilterDatasetBackupTuple(
        Long securityFilterDatasetId,
        Long securityFilterId,
        Long dataSetId,
        LocalDateTime created,
        LocalDateTime modified
) {
}
