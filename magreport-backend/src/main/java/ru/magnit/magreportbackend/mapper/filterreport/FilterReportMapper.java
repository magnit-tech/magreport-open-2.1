package ru.magnit.magreportbackend.mapper.filterreport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstance;
import ru.magnit.magreportbackend.domain.filterreport.FilterReport;
import ru.magnit.magreportbackend.domain.filterreport.FilterReportMode;
import ru.magnit.magreportbackend.domain.filterreport.FilterReportModeType;
import ru.magnit.magreportbackend.dto.request.filterreport.FilterAddRequest;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterReportMapper implements Mapper<FilterReport, FilterAddRequest> {

    private final FilterReportFieldMapper fieldMapper;

    @Override
    public FilterReport from(FilterAddRequest source) {
        var filterReport = new FilterReport()
                .setName(source.getName())
                .setCode(source.getCode())
                .setDescription(source.getDescription())
                .setOrdinal(source.getOrdinal())
                .setMandatory(source.getMandatory())
                .setHidden(source.getHidden())
                .setRootSelectable(source.getRootSelectable())
                .setFilterInstance(new FilterInstance(source.getFilterInstanceId()))
                .setFields(fieldMapper.from(source.getFields()))
                .setMaxCountItems(source.getMaxCountItems())
                .setModes(source.getFilterReportModes().stream().map(m -> new FilterReportMode(new FilterReportModeType(m.getId()))).toList());
        filterReport.getFields().forEach(field -> field.setFilterReport(filterReport));
        filterReport.getModes().forEach( mode -> mode.setFilterReport(filterReport));

        return filterReport;
    }
}
