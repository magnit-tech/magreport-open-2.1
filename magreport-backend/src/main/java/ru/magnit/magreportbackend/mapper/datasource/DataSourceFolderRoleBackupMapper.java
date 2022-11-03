package ru.magnit.magreportbackend.mapper.datasource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.datasource.DataSourceFolderRole;
import ru.magnit.magreportbackend.dto.backup.datasource.DataSourceFolderRoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSourceFolderRoleBackupMapper implements Mapper<DataSourceFolderRoleBackupTuple, DataSourceFolderRole> {
    @Override
    public DataSourceFolderRoleBackupTuple from(DataSourceFolderRole source) {
        return new DataSourceFolderRoleBackupTuple(
                source.getId(),
                source.getFolder().getId(),
                source.getRole().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
