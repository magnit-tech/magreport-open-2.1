package ru.magnit.magreportbackend.mapper.olap;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.olap.OlapConfiguration;
import ru.magnit.magreportbackend.dto.backup.olap.OlapConfigurationBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class OlapConfigurationBackupMapper implements Mapper<OlapConfigurationBackupTuple, OlapConfiguration> {
    @Override
    public OlapConfigurationBackupTuple from(OlapConfiguration source) {
        return new OlapConfigurationBackupTuple(
                source.getId(),
                source.getUser().getId(),
                source.getData(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()

        );
    }
}
