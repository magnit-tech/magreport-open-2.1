package ru.magnit.magreportbackend.dto.backup.folder;

import java.time.LocalDateTime;

public record FolderRoleBackupTuple(

        Long folderRoleId,
        Long folderId,
        Long roleId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
