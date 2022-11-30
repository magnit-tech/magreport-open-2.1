package ru.magnit.magreportbackend.mapper.datasource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.datasource.DataSource;
import ru.magnit.magreportbackend.domain.datasource.DataSourceFolder;
import ru.magnit.magreportbackend.domain.datasource.DataSourceType;
import ru.magnit.magreportbackend.dto.backup.datasource.DataSourceBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSourceUpMapper implements Mapper<DataSource, DataSourceBackupTuple> {
    @Override
    public DataSource from(DataSourceBackupTuple source) {
        return new DataSource()
                .setId(source.dataSourceId())
                .setFolder(new DataSourceFolder(source.dataSourceFolderId()))
                .setUserName(source.userName())
                .setPassword("")
                .setType(new DataSourceType(source.dataSourceTypeId()))
                .setPoolSize(source.poolSize())
                .setUrl(source.jdbcUrl())
                .setName(source.name())
                .setDescription(source.description());
    }
}
