package ru.magnit.magreportbackend.mapper.filterreport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filterreport.BooleanOperation;
import ru.magnit.magreportbackend.domain.filterreport.FilterReportGroup;
import ru.magnit.magreportbackend.domain.report.Report;
import ru.magnit.magreportbackend.dto.backup.filterreport.FilterReportGroupBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterReportGroupRestoreMapper implements Mapper<FilterReportGroup, FilterReportGroupBackupTuple> {
    @Override
    public FilterReportGroup from(FilterReportGroupBackupTuple source) {
        return new FilterReportGroup()
                .setId(source.filterReportGroupId())
                .setCode(source.code())
                .setName(source.name())
                .setDescription(source.description())
                .setReport(new Report(source.reportId()))
                .setParentGroup(source.parentId() == null ? null : new FilterReportGroup(source.parentId()))
                .setLinkedFilters(source.linkedFilters())
                .setMandatory(source.mandatory())
                .setOrdinal(source.ordinal())
                .setType(new BooleanOperation(source.reportFilterGroupOperationTypeId()));
    }
}
