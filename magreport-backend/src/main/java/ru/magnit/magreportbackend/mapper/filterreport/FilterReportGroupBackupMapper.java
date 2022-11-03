package ru.magnit.magreportbackend.mapper.filterreport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filterreport.FilterReportGroup;
import ru.magnit.magreportbackend.dto.backup.filterreport.FilterReportGroupBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterReportGroupBackupMapper implements Mapper<FilterReportGroupBackupTuple, FilterReportGroup> {
    @Override
    public FilterReportGroupBackupTuple from(FilterReportGroup source) {
        return new FilterReportGroupBackupTuple(
                source.getId(),
                source.getParentGroup() == null ? null : source.getParentGroup().getId(),
                source.getReport().getId(),
                source.getType().getId(),
                source.getOrdinal(),
                source.getLinkedFilters(),
                source.getMandatory(),
                source.getCode(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
