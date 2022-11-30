package ru.magnit.magreportbackend.mapper.dataset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSetFolder;
import ru.magnit.magreportbackend.dto.backup.dataset.DatasetFolderBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSetFolderUpMapper implements Mapper<DataSetFolder, DatasetFolderBackupTuple> {
    @Override
    public DataSetFolder from(DatasetFolderBackupTuple source) {
        return new DataSetFolder()
                .setId(source.datasetFolderId())
                .setParentFolder(new DataSetFolder(source.parentId()))
                .setName(source.name())
                .setDescription(source.description());
    }
}
