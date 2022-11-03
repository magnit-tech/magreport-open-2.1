package ru.magnit.magreportbackend.mapper.filtertemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTemplateFolderRole;
import ru.magnit.magreportbackend.dto.backup.filtertemplate.FilterTemplateFolderRoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterTemplateFolderRoleBackupMapper implements Mapper<FilterTemplateFolderRoleBackupTuple, FilterTemplateFolderRole> {
    @Override
    public FilterTemplateFolderRoleBackupTuple from(FilterTemplateFolderRole source) {
        return new FilterTemplateFolderRoleBackupTuple(
                source.getId(),
                source.getFolder().getId(),
                source.getRole().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
