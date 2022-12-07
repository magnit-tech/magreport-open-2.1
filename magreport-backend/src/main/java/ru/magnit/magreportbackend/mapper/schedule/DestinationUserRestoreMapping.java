package ru.magnit.magreportbackend.mapper.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.schedule.DestinationType;
import ru.magnit.magreportbackend.domain.schedule.DestinationUser;
import ru.magnit.magreportbackend.dto.backup.schedule.DestinationUserBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DestinationUserRestoreMapping implements Mapper<DestinationUser, DestinationUserBackupTuple> {
    @Override
    public DestinationUser from(DestinationUserBackupTuple source) {
        return new DestinationUser(source.val(), source.domain())
                .setType(new DestinationType(source.destinationTypeId()));
    }
}
