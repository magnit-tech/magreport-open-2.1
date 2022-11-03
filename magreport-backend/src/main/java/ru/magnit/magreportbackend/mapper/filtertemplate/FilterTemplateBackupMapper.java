package ru.magnit.magreportbackend.mapper.filtertemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTemplate;
import ru.magnit.magreportbackend.dto.backup.filtertemplate.FilterTemplateBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterTemplateBackupMapper implements Mapper<FilterTemplateBackupTuple, FilterTemplate> {
    @Override
    public FilterTemplateBackupTuple from(FilterTemplate source) {
        return new FilterTemplateBackupTuple(
                source.getId(),
                source.getType().getId(),
                source.getFolder().getId(),
                source.getUser().getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
