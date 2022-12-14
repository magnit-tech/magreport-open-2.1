package ru.magnit.magreportbackend.mapper.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.schedule.DestinationRole;
import ru.magnit.magreportbackend.domain.schedule.DestinationType;
import ru.magnit.magreportbackend.dto.backup.schedule.DestinationRoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DestinationRoleRestoreMapping implements Mapper<DestinationRole, DestinationRoleBackupTuple> {
    @Override
    public DestinationRole from(DestinationRoleBackupTuple source) {
        return new DestinationRole()
                .setValue(source.val())
                .setName(source.name())
                .setType(new DestinationType(source.destinationTypeId()));
    }
}
