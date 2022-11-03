package ru.magnit.magreportbackend.mapper.dataset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSetFolderRolePermission;
import ru.magnit.magreportbackend.dto.backup.dataset.DataSetFolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DataSetFolderRolePermissionBackupMapper implements Mapper<DataSetFolderRolePermissionBackupTuple, DataSetFolderRolePermission> {
    @Override
    public DataSetFolderRolePermissionBackupTuple from(DataSetFolderRolePermission source) {
        return new DataSetFolderRolePermissionBackupTuple(
                source.getId(),
                source.getFolderRole().getId(),
                source.getAuthority().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
