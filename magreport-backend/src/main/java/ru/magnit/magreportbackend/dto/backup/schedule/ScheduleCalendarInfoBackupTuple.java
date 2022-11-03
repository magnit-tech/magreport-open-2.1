package ru.magnit.magreportbackend.dto.backup.schedule;

import java.time.LocalDate;

public record ScheduleCalendarInfoBackupTuple(

        LocalDate date,
        int dayId,
        int monthId,
        int yearId,
        int dayWeek,
        int dayEndMonth,
        int weekMonth,
        int weekEndMonth

) {
}
