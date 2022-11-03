package ru.magnit.magreportbackend.mapper.filtertemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTemplateFolderRolePermission;
import ru.magnit.magreportbackend.dto.backup.filtertemplate.FilterTemplateFolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterTemplateFolderRolePermissionBackupMapper implements Mapper<FilterTemplateFolderRolePermissionBackupTuple, FilterTemplateFolderRolePermission> {
    @Override
    public FilterTemplateFolderRolePermissionBackupTuple from(FilterTemplateFolderRolePermission source) {
        return new FilterTemplateFolderRolePermissionBackupTuple(
                source.getId(),
                source.getFolderRole().getId(),
                source.getAuthority().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
