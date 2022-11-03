package ru.magnit.magreportbackend.dto.backup.folder;

import java.time.LocalDateTime;

public record FolderReportBackupTuple (

        Long folderReportId,
        Long folderId,
        Long reportId,
        Long userId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
