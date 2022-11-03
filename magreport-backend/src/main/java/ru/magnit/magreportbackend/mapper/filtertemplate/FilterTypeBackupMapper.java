package ru.magnit.magreportbackend.mapper.filtertemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterType;
import ru.magnit.magreportbackend.dto.backup.filterreport.FilterTypeBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterTypeBackupMapper implements Mapper<FilterTypeBackupTuple, FilterType> {
    @Override
    public FilterTypeBackupTuple from(FilterType source) {
        return new FilterTypeBackupTuple(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
