package ru.magnit.magreportbackend.dto.backup.report;

import java.time.LocalDateTime;

public record ReportFolderRoleBackupTuple(

        Long reportFolderRoleId,
        Long reportFolderId,
        Long roleId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
