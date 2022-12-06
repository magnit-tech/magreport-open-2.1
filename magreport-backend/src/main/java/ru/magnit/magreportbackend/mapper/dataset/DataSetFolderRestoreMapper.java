package ru.magnit.magreportbackend.mapper.dataset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSetFolder;
import ru.magnit.magreportbackend.dto.backup.dataset.DataSetFolderBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSetFolderRestoreMapper implements Mapper<DataSetFolder, DataSetFolderBackupTuple> {
    @Override
    public DataSetFolder from(DataSetFolderBackupTuple source) {
        return new DataSetFolder()
                .setId(source.datasetFolderId())
                .setParentFolder(new DataSetFolder(source.parentId()))
                .setName(source.name())
                .setDescription(source.description());
    }
}
