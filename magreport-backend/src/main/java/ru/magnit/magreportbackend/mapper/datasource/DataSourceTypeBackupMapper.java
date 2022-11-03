package ru.magnit.magreportbackend.mapper.datasource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.datasource.DataSourceType;
import ru.magnit.magreportbackend.dto.backup.datasource.DataSourceTypeBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSourceTypeBackupMapper implements Mapper<DataSourceTypeBackupTuple, DataSourceType> {
    @Override
    public DataSourceTypeBackupTuple from(DataSourceType source) {
        return new DataSourceTypeBackupTuple(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
