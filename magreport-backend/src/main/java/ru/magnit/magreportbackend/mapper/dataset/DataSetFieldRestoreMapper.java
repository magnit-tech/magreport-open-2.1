package ru.magnit.magreportbackend.mapper.dataset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSetField;
import ru.magnit.magreportbackend.domain.dataset.DataType;
import ru.magnit.magreportbackend.dto.backup.dataset.DataSetFieldBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSetFieldRestoreMapper implements Mapper<DataSetField, DataSetFieldBackupTuple> {
    @Override
    public DataSetField from(DataSetFieldBackupTuple source) {
        return new DataSetField()
                .setId(source.datasetFieldId())
                .setType(new DataType(source.dataTypeId()))
                .setIsSync(source.isSync())
                .setName(source.name())
                .setDescription(source.description());
    }
}
