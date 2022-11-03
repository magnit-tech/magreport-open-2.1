package ru.magnit.magreportbackend.mapper.filterreport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filterreport.FilterReportField;
import ru.magnit.magreportbackend.dto.backup.filterreport.FilterReportFieldBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterReportFieldBackupMapper implements Mapper<FilterReportFieldBackupTuple, FilterReportField> {
    @Override
    public FilterReportFieldBackupTuple from(FilterReportField source) {
        return new FilterReportFieldBackupTuple(
                source.getId(),
                source.getFilterReport().getId(),
                source.getFilterInstanceField().getId(),
                source.getReportField() == null ? null : source.getReportField().getId(),
                source.getOrdinal(),
                source.isExpand(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()

        );
    }
}
