package ru.magnit.magreportbackend.mapper.dataset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSet;
import ru.magnit.magreportbackend.dto.backup.dataset.DataSetBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSetBackupMapper implements Mapper<DataSetBackupTuple, DataSet> {

    @Override
    public DataSetBackupTuple from(DataSet source) {
        return new DataSetBackupTuple(
                source.getId(),
                source.getDataSource().getId(),
                source.getType().getId(),
                source.getFolder().getId(),
                source.getCatalogName(),
                source.getSchemaName(),
                source.getObjectName(),
                source.getName(),
                source.getDescription(),
                source.getDomainName(),
                source.getUser().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
