package ru.magnit.magreportbackend.dto.backup.filtertemplate;

import java.time.LocalDateTime;

public record FilterTemplateFolderRoleBackupTuple(

        Long filterTemplateFolderRoleId,
        Long filterTemplateFolderId,
        Long roleId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
