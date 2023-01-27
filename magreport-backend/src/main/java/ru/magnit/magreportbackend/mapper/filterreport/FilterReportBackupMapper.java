package ru.magnit.magreportbackend.mapper.filterreport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filterreport.FilterReport;
import ru.magnit.magreportbackend.dto.backup.filterreport.FilterReportBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterReportBackupMapper implements Mapper<FilterReportBackupTuple, FilterReport> {
    @Override
    public FilterReportBackupTuple from(FilterReport source) {
        return new FilterReportBackupTuple(
                source.getId(),
                source.getGroup().getId(),
                source.getFilterInstance().getId(),
                source.getOrdinal(),
                source.getUser().getId(),
                source.getMaxCountItems(),
                source.isHidden(),
                source.isMandatory(),
                source.isRootSelectable(),
                source.getCode(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
