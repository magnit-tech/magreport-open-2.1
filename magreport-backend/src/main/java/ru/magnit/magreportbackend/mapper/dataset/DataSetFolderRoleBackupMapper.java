package ru.magnit.magreportbackend.mapper.dataset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSetFolderRole;
import ru.magnit.magreportbackend.dto.backup.dataset.DatasetFolderRoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSetFolderRoleBackupMapper implements Mapper<DatasetFolderRoleBackupTuple, DataSetFolderRole> {
    @Override
    public DatasetFolderRoleBackupTuple from(DataSetFolderRole source) {
        return new DatasetFolderRoleBackupTuple(
                source.getId(),
                source.getFolder().getId(),
                source.getRole().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
