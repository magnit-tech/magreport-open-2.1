package ru.magnit.magreportbackend.mapper.filterinstance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstance;
import ru.magnit.magreportbackend.dto.backup.filterinstance.FilterInstanceBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterInstanceBackupMapper implements Mapper<FilterInstanceBackupTuple, FilterInstance> {
    @Override
    public FilterInstanceBackupTuple from(FilterInstance source) {
        return new FilterInstanceBackupTuple(
                source.getId(),
                source.getFolder().getId(),
                source.getFilterTemplate().getId(),
                source.getDataSet() == null ? null : source.getDataSet().getId(),
                source.getUser().getId(),
                source.getName(),
                source.getDescription(),
                source.getCode(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
