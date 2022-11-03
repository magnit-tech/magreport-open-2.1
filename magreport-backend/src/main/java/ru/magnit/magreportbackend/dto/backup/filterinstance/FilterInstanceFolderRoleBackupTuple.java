package ru.magnit.magreportbackend.dto.backup.filterinstance;

import java.time.LocalDateTime;

public record FilterInstanceFolderRoleBackupTuple(


        Long filterInstanceFolderRoleId,
        Long filterInstanceFolderId,
        Long roleId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
