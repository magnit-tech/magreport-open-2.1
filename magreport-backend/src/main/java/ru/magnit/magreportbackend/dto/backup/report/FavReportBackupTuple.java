package ru.magnit.magreportbackend.dto.backup.report;

import java.time.LocalDateTime;

public record FavReportBackupTuple(

        Long favReportId,
        Long userId,
        Long folderId,
        Long reportId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
