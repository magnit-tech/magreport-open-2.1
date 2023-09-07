package ru.magnit.magreportbackend.mapper.filterinstance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceField;
import ru.magnit.magreportbackend.dto.backup.filterinstance.FilterInstanceFieldBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterInstanceFieldBackupMapper implements Mapper<FilterInstanceFieldBackupTuple, FilterInstanceField> {
    @Override
    public FilterInstanceFieldBackupTuple from(FilterInstanceField source) {
        return new FilterInstanceFieldBackupTuple(
                source.getId(),
                source.getLevel(),
                source.getInstance().getId(),
                source.getTemplateField().getId(),
                source.getDataSetField() == null ?  null : source.getDataSetField().getId(),
                source.getExpand(),
                source.getName(),
                source.getDescription(),
                source.getShowField(),
                source.getSearchByField(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
