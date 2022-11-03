package ru.magnit.magreportbackend.mapper.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.schedule.ScheduleCalendarInfo;
import ru.magnit.magreportbackend.dto.backup.schedule.ScheduleCalendarInfoBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ScheduleCalendarInfoBackupMapper implements Mapper<ScheduleCalendarInfoBackupTuple, ScheduleCalendarInfo> {
    @Override
    public ScheduleCalendarInfoBackupTuple from(ScheduleCalendarInfo source) {
        return new ScheduleCalendarInfoBackupTuple(
                source.getDate(),
                source.getDay(),
                source.getMonth(),
                source.getYear(),
                source.getDayWeek(),
                source.getNumDayEndMonth(),
                source.getNumWeekMonth(),
                source.getNumWeekEndMonth()
        );
    }
}
