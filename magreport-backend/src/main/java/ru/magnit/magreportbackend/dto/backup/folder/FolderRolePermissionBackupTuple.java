package ru.magnit.magreportbackend.dto.backup.folder;

import java.time.LocalDateTime;

public record FolderRolePermissionBackupTuple(

        Long folderRolePermissionId,
        Long folderRoleId,
        Long folderAuthorityId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
