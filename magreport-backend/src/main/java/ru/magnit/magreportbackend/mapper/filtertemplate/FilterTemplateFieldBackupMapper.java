package ru.magnit.magreportbackend.mapper.filtertemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTemplateField;
import ru.magnit.magreportbackend.dto.backup.filtertemplate.FilterTemplateFieldBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterTemplateFieldBackupMapper implements Mapper<FilterTemplateFieldBackupTuple, FilterTemplateField> {
    @Override
    public FilterTemplateFieldBackupTuple from(FilterTemplateField source) {
        return new FilterTemplateFieldBackupTuple(
                source.getId(),
                source.getFilterTemplate().getId(),
                source.getType().getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
