package ru.magnit.magreportbackend.mapper.filterinstance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSetField;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstance;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceField;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTemplateField;
import ru.magnit.magreportbackend.dto.backup.filterinstance.FilterInstanceFieldBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterInstanceFieldRestoreMapper implements Mapper<FilterInstanceField, FilterInstanceFieldBackupTuple> {
    @Override
    public FilterInstanceField from(FilterInstanceFieldBackupTuple source) {
        return new FilterInstanceField()
                .setId(source.filterInstanceFieldId())
                .setName(source.name())
                .setDescription(source.description())
                .setExpand(source.expand())
                .setLevel(source.level())
                .setInstance(new FilterInstance(source.filterInstanceId()))
                .setTemplateField(new FilterTemplateField(source.filterTemplateFieldId()))
                .setDataSetField(new DataSetField(source.datasetFieldId()));
    }
}
