package ru.magnit.magreportbackend.mapper.datasource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.datasource.DataSourceFolder;
import ru.magnit.magreportbackend.dto.backup.datasource.DataSourceFolderBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSourceFolderBackupMapper implements Mapper<DataSourceFolderBackupTuple, DataSourceFolder> {
    @Override
    public DataSourceFolderBackupTuple from(DataSourceFolder source) {
        return new DataSourceFolderBackupTuple(
                source.getId(),
                source.getParentFolder() == null ? null : source.getParentFolder().getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
