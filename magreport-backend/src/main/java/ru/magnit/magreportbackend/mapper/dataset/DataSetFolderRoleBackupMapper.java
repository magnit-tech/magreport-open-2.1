package ru.magnit.magreportbackend.mapper.dataset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSetFolderRole;
import ru.magnit.magreportbackend.dto.backup.dataset.DataSetFolderRoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSetFolderRoleBackupMapper implements Mapper<DataSetFolderRoleBackupTuple, DataSetFolderRole> {
    @Override
    public DataSetFolderRoleBackupTuple from(DataSetFolderRole source) {
        return new DataSetFolderRoleBackupTuple(
                source.getId(),
                source.getFolder().getId(),
                source.getRole().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
