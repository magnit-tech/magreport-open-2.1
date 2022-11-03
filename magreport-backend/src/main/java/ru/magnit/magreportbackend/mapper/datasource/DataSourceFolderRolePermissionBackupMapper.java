package ru.magnit.magreportbackend.mapper.datasource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.datasource.DataSourceFolderRolePermission;
import ru.magnit.magreportbackend.dto.backup.datasource.DataSourceFolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSourceFolderRolePermissionBackupMapper implements Mapper<DataSourceFolderRolePermissionBackupTuple, DataSourceFolderRolePermission> {
    @Override
    public DataSourceFolderRolePermissionBackupTuple from(DataSourceFolderRolePermission source) {
        return new DataSourceFolderRolePermissionBackupTuple(
                source.getId(),
                source.getFolderRole().getId(),
                source.getAuthority().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
