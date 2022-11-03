package ru.magnit.magreportbackend.mapper.dataset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSetFolder;
import ru.magnit.magreportbackend.dto.backup.dataset.DatasetFolderBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSetFolderBackupMapper implements Mapper<DatasetFolderBackupTuple, DataSetFolder> {
    @Override
    public DatasetFolderBackupTuple from(DataSetFolder source) {
        return new DatasetFolderBackupTuple(
                source.getId(),
                source.getParentFolder() == null ? null : source.getParentFolder().getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
