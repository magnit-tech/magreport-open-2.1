package ru.magnit.magreportbackend.dto.backup.schedule;

import java.time.LocalDateTime;

public record ScheduleTypeBackupTuple(
        Long scheduleTypeId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
