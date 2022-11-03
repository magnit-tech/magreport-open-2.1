package ru.magnit.magreportbackend.dto.backup.securityfilter;

import java.time.LocalDateTime;

public record SecurityFilterBackupTuple(

        Long securityFilterId,
        Long filterInstanceId,
        Long filterOperationTypeId,
        Long securityFilterFolderId,
        Long userId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
