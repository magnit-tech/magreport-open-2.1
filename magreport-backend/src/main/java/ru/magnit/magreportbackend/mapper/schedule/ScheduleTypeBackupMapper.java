package ru.magnit.magreportbackend.mapper.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.schedule.ScheduleType;
import ru.magnit.magreportbackend.dto.backup.schedule.ScheduleTypeBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ScheduleTypeBackupMapper implements Mapper<ScheduleTypeBackupTuple, ScheduleType> {
    @Override
    public ScheduleTypeBackupTuple from(ScheduleType source) {
        return new ScheduleTypeBackupTuple(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
