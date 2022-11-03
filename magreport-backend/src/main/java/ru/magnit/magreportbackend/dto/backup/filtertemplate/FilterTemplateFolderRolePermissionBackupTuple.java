package ru.magnit.magreportbackend.dto.backup.filtertemplate;

import java.time.LocalDateTime;

public record FilterTemplateFolderRolePermissionBackupTuple(

        Long filterTemplateFolderRolePermissionId,
        Long filterTemplateFolderRoleId,
        Long folderAuthorityId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
