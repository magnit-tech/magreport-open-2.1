package ru.magnit.magreportbackend.dto.backup.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleTaskBackupTuple (
        Long scheduleTaskId,
        Long reportId,
        Long scheduleTaskTypeId,
        Long scheduleTaskStatusId,
        Long renewalPeriod,
        Long excelTemplateId,
        Long userId,
        Long failedStarts,
        Long maxFailedStarts,
        UUID expirationCode,
        String code,
        String reportBodyMail,
        String name,
        String description,
        String reportTitleMail,
        String errorBodyMail,
        String errorTitleMail,
        boolean sendExpiredMail,
        boolean sendEmptyReport,
        LocalDate expirationDate,
        LocalDateTime created,
        LocalDateTime modified
) {
}
