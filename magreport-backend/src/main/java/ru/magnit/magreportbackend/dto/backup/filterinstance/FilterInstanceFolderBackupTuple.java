package ru.magnit.magreportbackend.dto.backup.filterinstance;

import java.time.LocalDateTime;

public record FilterInstanceFolderBackupTuple(

        Long filterInstanceFolderId,
        Long parentId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified) {
}
