package ru.magnit.magreportbackend.mapper.dataset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSet;
import ru.magnit.magreportbackend.domain.dataset.DataSetFolder;
import ru.magnit.magreportbackend.domain.dataset.DataSetType;
import ru.magnit.magreportbackend.domain.datasource.DataSource;
import ru.magnit.magreportbackend.dto.backup.dataset.DatasetBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSetRestoreMapper implements Mapper<DataSet, DatasetBackupTuple> {
    @Override
    public DataSet from(DatasetBackupTuple source) {
        return new DataSet()
                .setId(source.datasetId())
                .setFolder(new DataSetFolder(source.datasetFolderId()))
                .setType(new DataSetType(source.datasetTypeId()))
                .setCatalogName(source.catalogName())
                .setObjectName(source.objectName())
                .setSchemaName(source.schemaName())
                .setName(source.name())
                .setDescription(source.description())
                .setDataSource(new DataSource(source.datasourceId()));
    }
}
