package ru.magnit.magreportbackend.mapper.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.schedule.Schedule;
import ru.magnit.magreportbackend.domain.schedule.ScheduleType;
import ru.magnit.magreportbackend.dto.backup.schedule.ScheduleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ScheduleRestoreMapper implements Mapper<Schedule, ScheduleBackupTuple> {
    @Override
    public Schedule from(ScheduleBackupTuple source) {
        return new Schedule()
                .setId(source.scheduleId())
                .setType(new ScheduleType(source.scheduleTypeId()))
                .setName(source.name())
                .setDescription(source.description())
                .setSecond(source.secondId())
                .setMinute(source.minuteId())
                .setHour(source.hourId())
                .setDay(source.dayId())
                .setDayWeek(source.dayWeek())
                .setDayEndMonth(source.dayEndMonth())
                .setWeekMonth(source.weekMonth())
                .setWeekEndMonth(source.weekEndMonth())
                .setDifferenceTime(source.differenceTime())
                .setPlanStartDate(source.planStartDate())
                .setLastStartDate(source.lastStartDate());
    }
}
