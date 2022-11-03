package ru.magnit.magreportbackend.mapper.dataset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSet;
import ru.magnit.magreportbackend.dto.backup.dataset.DatasetBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DataSetBackupMapper implements Mapper<DatasetBackupTuple, DataSet> {

    @Override
    public DatasetBackupTuple from(DataSet source) {
        return new DatasetBackupTuple(
                source.getId(),
                source.getDataSource().getId(),
                source.getType().getId(),
                source.getFolder().getId(),
                source.getCatalogName(),
                source.getSchemaName(),
                source.getObjectName(),
                source.getName(),
                source.getDescription(),
                source.getUser().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
