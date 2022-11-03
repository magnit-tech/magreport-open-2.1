package ru.magnit.magreportbackend.mapper.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.schedule.DestinationType;
import ru.magnit.magreportbackend.dto.backup.schedule.DestinationTypeBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DestinationTypeBackupMapper implements Mapper<DestinationTypeBackupTuple, DestinationType> {
    @Override
    public DestinationTypeBackupTuple from(DestinationType source) {
        return new DestinationTypeBackupTuple(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
