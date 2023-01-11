package ru.magnit.magreportbackend.mapper.report;

import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.report.ReportField;
import ru.magnit.magreportbackend.dto.response.report.ReportFieldTypeResponse;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
public class ReportFieldTypeResponseMapper implements Mapper<ReportFieldTypeResponse, ReportField> {

    @Override
    public ReportFieldTypeResponse from(ReportField source) {
        return new ReportFieldTypeResponse(
            source.getId(),
            source.getVisible(),
            source.getOrdinal(),
            source.getDataSetField().getType().getEnum()
        );
    }
}
