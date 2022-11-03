package ru.magnit.magreportbackend.mapper.securityfilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterFolderRolePermission;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterFolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class SecurityFilterFolderRolePermissionBackupMapper implements Mapper<SecurityFilterFolderRolePermissionBackupTuple, SecurityFilterFolderRolePermission> {
    @Override
    public SecurityFilterFolderRolePermissionBackupTuple from(SecurityFilterFolderRolePermission source) {
        return new SecurityFilterFolderRolePermissionBackupTuple(
                source.getId(),
                source.getFolderRole().getId(),
                source.getAuthority().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
