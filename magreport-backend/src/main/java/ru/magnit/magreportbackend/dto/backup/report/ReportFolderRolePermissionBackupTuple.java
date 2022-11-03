package ru.magnit.magreportbackend.dto.backup.report;

import java.time.LocalDateTime;

public record ReportFolderRolePermissionBackupTuple(

        Long reportFolderRolePermissionId,
        Long reportFolderRoleId,
        Long folderAuthorityId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
