package ru.magnit.magreportbackend.dto.backup.user;

import java.time.LocalDateTime;

public record UserReportExcelTemplateBackupTuple(
        Long userReportExcelTemplateId,
        Long userId,
        Long reportId,
        Long reportExcelTemplateId,
        LocalDateTime created,
        LocalDateTime modified
) {
}
