package ru.magnit.magreportbackend.mapper.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.schedule.ScheduleTaskStatus;
import ru.magnit.magreportbackend.dto.backup.schedule.ScheduleTaskStatusBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ScheduleTaskStatusBackupMapper implements Mapper<ScheduleTaskStatusBackupTuple, ScheduleTaskStatus> {
    @Override
    public ScheduleTaskStatusBackupTuple from(ScheduleTaskStatus source) {
        return new ScheduleTaskStatusBackupTuple(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
