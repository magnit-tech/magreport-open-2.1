package ru.magnit.magreportbackend.mapper.filterinstance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSet;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstance;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceFolder;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTemplate;
import ru.magnit.magreportbackend.dto.backup.filterinstance.FilterInstanceBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterInstanceRestoreMapper implements Mapper<FilterInstance, FilterInstanceBackupTuple> {
    @Override
    public FilterInstance from(FilterInstanceBackupTuple source) {
        return new FilterInstance()
                .setId(source.filterInstanceId())
                .setCode(source.code())
                .setName(source.name())
                .setDescription(source.description())
                .setDataSet(source.datasetId() == null? null : new DataSet(source.datasetId()))
                .setFilterTemplate(new FilterTemplate(source.filterTemplateId()))
                .setFolder(new FilterInstanceFolder(source.filterInstanceFolderId()));
    }
}
