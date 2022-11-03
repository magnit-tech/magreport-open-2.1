package ru.magnit.magreportbackend.dto.backup.securityfilter;

import java.time.LocalDateTime;

public record SecurityFilterFolderRolePermissionBackupTuple(
        Long securityFilterFolderRolePermissionId,
        Long securityFilterFolderRoleId,
        Long folderAuthorityId,
        LocalDateTime created,
        LocalDateTime modified
) {
}
