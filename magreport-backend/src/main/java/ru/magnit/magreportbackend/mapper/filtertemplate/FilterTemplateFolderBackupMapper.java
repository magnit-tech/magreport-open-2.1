package ru.magnit.magreportbackend.mapper.filtertemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTemplateFolder;
import ru.magnit.magreportbackend.dto.backup.filtertemplate.FilterTemplateFolderBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterTemplateFolderBackupMapper implements Mapper<FilterTemplateFolderBackupTuple, FilterTemplateFolder> {
    @Override
    public FilterTemplateFolderBackupTuple from(FilterTemplateFolder source) {
        return new FilterTemplateFolderBackupTuple(
                source.getId(),
                source.getParentFolder() == null ? null : source.getParentFolder().getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
