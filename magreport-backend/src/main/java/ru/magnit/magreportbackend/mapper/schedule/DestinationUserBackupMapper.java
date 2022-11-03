package ru.magnit.magreportbackend.mapper.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.schedule.DestinationUser;
import ru.magnit.magreportbackend.dto.backup.schedule.DestinationUserBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DestinationUserBackupMapper implements Mapper<DestinationUserBackupTuple, DestinationUser> {
    @Override
    public DestinationUserBackupTuple from(DestinationUser source) {
        return new DestinationUserBackupTuple(
                source.getId(),
                source.getScheduleTask().getId(),
                source.getType().getId(),
                source.getValue(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
