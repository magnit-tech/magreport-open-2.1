package ru.magnit.magreportbackend.dto.backup.securityfilter;

import java.time.LocalDateTime;

public record SecurityFilterDatasetFieldBackupTuple(
        Long securityFilterDataSetFieldId,
        Long securityFilterId,
        Long filterInstanceFieldId,
        Long datasetFieldId,
        LocalDateTime created,
        LocalDateTime modified
) {
}
