package ru.magnit.magreportbackend.mapper.dataset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataType;
import ru.magnit.magreportbackend.dto.backup.dataset.DataTypeBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataTypeBackupMapper implements Mapper<DataTypeBackupTuple, DataType> {

    @Override
    public DataTypeBackupTuple from(DataType source) {
        return new DataTypeBackupTuple(
            source.getId(),
            source.getName(),
            source.getDescription(),
            source.getCreatedDateTime(),
            source.getModifiedDateTime()
        );
    }
}
