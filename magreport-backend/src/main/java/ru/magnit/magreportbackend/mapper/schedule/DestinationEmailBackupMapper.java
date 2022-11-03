package ru.magnit.magreportbackend.mapper.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.schedule.DestinationEmail;
import ru.magnit.magreportbackend.dto.backup.schedule.DestinationEmailBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DestinationEmailBackupMapper implements Mapper<DestinationEmailBackupTuple, DestinationEmail> {

    @Override
    public DestinationEmailBackupTuple from(DestinationEmail source) {
        return new DestinationEmailBackupTuple(
                source.getId(),
                source.getScheduleTask().getId(),
                source.getType().getId(),
                source.getValue(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
