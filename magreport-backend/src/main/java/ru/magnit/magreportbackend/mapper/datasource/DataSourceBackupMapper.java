package ru.magnit.magreportbackend.mapper.datasource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.datasource.DataSource;
import ru.magnit.magreportbackend.dto.backup.datasource.DataSourceBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSourceBackupMapper implements Mapper<DataSourceBackupTuple, DataSource> {
    @Override
    public DataSourceBackupTuple from(DataSource source) {
        return new DataSourceBackupTuple(
                source.getId(),
                source.getType().getId(),
                source.getFolder().getId(),
                source.getUser().getId(),
                source.getUrl(),
                source.getUserName(),
                null,
                source.getPoolSize(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
