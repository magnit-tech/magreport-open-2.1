package ru.magnit.magreportbackend.mapper.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportFieldData;
import ru.magnit.magreportbackend.dto.response.report.ReportFieldMetadataResponse;
import ru.magnit.magreportbackend.mapper.Mapper;
@Service
@RequiredArgsConstructor
public class ReportFieldMetadataMapper implements Mapper<ReportFieldMetadataResponse, ReportFieldData> {
    @Override
    public ReportFieldMetadataResponse  from(ReportFieldData source) {
        return new ReportFieldMetadataResponse(
                source.id(),
                source.dataType().toString(),
                source.name(),
                source.description(),
                source.ordinal(),
                source.visible()
        );
    }
}
