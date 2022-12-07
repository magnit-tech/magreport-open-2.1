package ru.magnit.magreportbackend.mapper.filterreport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstance;
import ru.magnit.magreportbackend.domain.filterreport.FilterReport;
import ru.magnit.magreportbackend.domain.filterreport.FilterReportGroup;
import ru.magnit.magreportbackend.dto.backup.filterreport.FilterReportBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterReportRestoreMapper implements Mapper<FilterReport, FilterReportBackupTuple> {
    @Override
    public FilterReport from(FilterReportBackupTuple source) {
        return new FilterReport()
                .setId(source.filterReportId())
                .setOrdinal(source.ordinal())
                .setName(source.name())
                .setDescription(source.description())
                .setCode(source.code())
                .setHidden(source.isHidden())
                .setMandatory(source.isMandatory())
                .setRootSelectable(source.isRootMandatory())
                .setGroup(new FilterReportGroup(source.filterReportGroupId()))
                .setFilterInstance(new FilterInstance(source.filterInstanceId()));
    }
}
