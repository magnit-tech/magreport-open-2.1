package ru.magnit.magreportbackend.dto.backup.schedule;

import java.time.LocalDateTime;

public record ScheduleTaskStatusBackupTuple(
        Long scheduleTaskStatusId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
