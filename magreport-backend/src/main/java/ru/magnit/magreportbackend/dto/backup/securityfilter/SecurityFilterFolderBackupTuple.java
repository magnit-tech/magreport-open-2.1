package ru.magnit.magreportbackend.dto.backup.securityfilter;

import java.time.LocalDateTime;

public record SecurityFilterFolderBackupTuple(
        Long securityFilterFolderId,
        Long parentId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
