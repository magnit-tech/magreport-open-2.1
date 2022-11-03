package ru.magnit.magreportbackend.dto.backup.securityfilter;

import java.time.LocalDateTime;

public record SecurityFilterFolderRoleBackupTuple(

        Long securityFilterFolderRoleId,
        Long securityFilterFolderId,
        Long roleId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
