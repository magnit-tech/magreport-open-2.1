package ru.magnit.magreportbackend.mapper.filterinstance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceFolderRolePermission;
import ru.magnit.magreportbackend.dto.backup.filterinstance.FilterInstanceFolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterInstanceFolderRolePermissionBackupMapper implements Mapper<FilterInstanceFolderRolePermissionBackupTuple, FilterInstanceFolderRolePermission> {
    @Override
    public FilterInstanceFolderRolePermissionBackupTuple from(FilterInstanceFolderRolePermission source) {
        return new FilterInstanceFolderRolePermissionBackupTuple(
                source.getId(),
                source.getFolderRole().getId(),
                source.getAuthority().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
