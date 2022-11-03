package ru.magnit.magreportbackend.dto.backup.filterinstance;

import java.time.LocalDateTime;

public record FilterInstanceFolderRolePermissionBackupTuple(

        Long filterInstanceFolderRolePermissionId,
        Long filterInstanceFolderRoleId,
        Long folderAuthorityId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
