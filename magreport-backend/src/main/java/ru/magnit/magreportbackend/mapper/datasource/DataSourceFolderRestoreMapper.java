package ru.magnit.magreportbackend.mapper.datasource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.datasource.DataSourceFolder;
import ru.magnit.magreportbackend.dto.backup.datasource.DataSourceFolderBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;
@Service
@RequiredArgsConstructor
public class DataSourceFolderRestoreMapper implements Mapper<DataSourceFolder, DataSourceFolderBackupTuple> {
    @Override
    public DataSourceFolder from(DataSourceFolderBackupTuple source) {
        return new DataSourceFolder()
                .setId(source.dataSourceFolderId())
                .setParentFolder(new DataSourceFolder(source.parentId()))
                .setName(source.name())
                .setDescription(source.description());
    }
}
