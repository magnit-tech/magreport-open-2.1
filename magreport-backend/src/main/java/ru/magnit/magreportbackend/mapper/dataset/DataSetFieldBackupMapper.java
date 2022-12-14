package ru.magnit.magreportbackend.mapper.dataset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSetField;
import ru.magnit.magreportbackend.dto.backup.dataset.DataSetFieldBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSetFieldBackupMapper implements Mapper<DataSetFieldBackupTuple, DataSetField> {
    @Override
    public DataSetFieldBackupTuple from(DataSetField source) {
        return new DataSetFieldBackupTuple(
                source.getId(),
                source.getDataSet().getId(),
                source.getType().getId(),
                source.getIsSync(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
