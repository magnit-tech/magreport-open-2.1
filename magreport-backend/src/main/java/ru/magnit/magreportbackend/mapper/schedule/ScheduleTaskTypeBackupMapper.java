package ru.magnit.magreportbackend.mapper.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.schedule.ScheduleTaskType;
import ru.magnit.magreportbackend.dto.backup.schedule.ScheduleTaskTypeBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ScheduleTaskTypeBackupMapper implements Mapper<ScheduleTaskTypeBackupTuple, ScheduleTaskType> {
    @Override
    public ScheduleTaskTypeBackupTuple from(ScheduleTaskType source) {
        return new ScheduleTaskTypeBackupTuple(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
