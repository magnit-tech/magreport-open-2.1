package ru.magnit.magreportbackend.dto.backup.schedule;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record ScheduleBackupTuple(
                Long scheduleId,
                Long scheduleTypeId,
                Long userId,
                Long secondId,
                Long minuteId,
                Long hourId,
                Long dayId,
                Long dayWeek,
                Long monthId,
                Long yearId,
                Long dayEndMonth,
                Long weekMonth,
                Long weekEndMonth,
                String name,
                String description,
                Long differenceTime,
                LocalDateTime planStartDate,
                LocalDateTime lastStartDate,
                LocalDateTime created,
                LocalDateTime modified,
                Long intervalMinutes,
                LocalTime finishTime

) {
}
