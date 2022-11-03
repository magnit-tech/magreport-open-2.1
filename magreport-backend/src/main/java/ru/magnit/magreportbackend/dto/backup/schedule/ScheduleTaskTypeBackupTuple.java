package ru.magnit.magreportbackend.dto.backup.schedule;

import java.time.LocalDateTime;

public record ScheduleTaskTypeBackupTuple(
        Long scheduleTaskTypeId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
