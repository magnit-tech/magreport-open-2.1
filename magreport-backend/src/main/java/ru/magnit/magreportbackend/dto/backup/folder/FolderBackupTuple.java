package ru.magnit.magreportbackend.dto.backup.folder;

import java.time.LocalDateTime;

public record FolderBackupTuple(

        Long folderId,
        Long parentId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
