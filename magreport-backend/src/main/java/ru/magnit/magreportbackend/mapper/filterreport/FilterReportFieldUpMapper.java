package ru.magnit.magreportbackend.mapper.filterreport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceField;
import ru.magnit.magreportbackend.domain.filterreport.FilterReport;
import ru.magnit.magreportbackend.domain.filterreport.FilterReportField;
import ru.magnit.magreportbackend.domain.report.ReportField;
import ru.magnit.magreportbackend.dto.backup.filterreport.FilterReportFieldBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterReportFieldUpMapper implements Mapper<FilterReportField, FilterReportFieldBackupTuple> {
    @Override
    public FilterReportField from(FilterReportFieldBackupTuple source) {
        return new FilterReportField()
                .setId(source.filterReportFieldId())
                .setName(source.name())
                .setDescription(source.description())
                .setExpand(source.expand())
                .setOrdinal(source.ordinal())
                .setFilterReport(new FilterReport(source.filterReportId()))
                .setFilterInstanceField(new FilterInstanceField(source.filterInstanceFieldId()))
                .setReportField(new ReportField(source.reportFieldId()));
    }
}
