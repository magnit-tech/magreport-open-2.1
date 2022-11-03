package ru.magnit.magreportbackend.mapper.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.schedule.Schedule;
import ru.magnit.magreportbackend.dto.backup.schedule.ScheduleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ScheduleBackupMapper implements Mapper<ScheduleBackupTuple, Schedule> {
    @Override
    public ScheduleBackupTuple from(Schedule source) {
        return new ScheduleBackupTuple(
                source.getId(),
                source.getType().getId(),
                source.getUser().getId(),
                source.getSecond(),
                source.getMinute(),
                source.getHour(),
                source.getDay(),
                source.getDayWeek(),
                source.getMonth(),
                source.getYear(),
                source.getDayEndMonth(),
                source.getWeekMonth(),
                source.getWeekEndMonth(),
                source.getName(),
                source.getDescription(),
                source.getDifferenceTime(),
                source.getPlanStartDate(),
                source.getLastStartDate(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
