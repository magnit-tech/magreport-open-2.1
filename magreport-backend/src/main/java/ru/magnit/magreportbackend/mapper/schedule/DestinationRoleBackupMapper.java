package ru.magnit.magreportbackend.mapper.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.schedule.DestinationRole;
import ru.magnit.magreportbackend.dto.backup.schedule.DestinationRoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DestinationRoleBackupMapper implements Mapper<DestinationRoleBackupTuple, DestinationRole> {
    @Override
    public DestinationRoleBackupTuple from(DestinationRole source) {
        return new DestinationRoleBackupTuple(
                source.getId(),
                source.getScheduleTask().getId(),
                source.getType().getId(),
                source.getValue(),
                source.getName(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
