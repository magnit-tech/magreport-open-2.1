package ru.magnit.magreportbackend.mapper.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.schedule.DestinationEmail;
import ru.magnit.magreportbackend.domain.schedule.DestinationType;
import ru.magnit.magreportbackend.dto.backup.schedule.DestinationEmailBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DestinationEmailRestoreMapping implements Mapper<DestinationEmail, DestinationEmailBackupTuple> {
    @Override
    public DestinationEmail from(DestinationEmailBackupTuple source) {
        return new DestinationEmail()
                .setType(new DestinationType(source.destinationTypeId()))
                .setValue(source.val());
    }
}
