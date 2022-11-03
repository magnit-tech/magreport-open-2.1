package ru.magnit.magreportbackend.service.domain.converter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportData;
import ru.magnit.magreportbackend.dto.request.olap.OlapExportPivotTableRequest;
import ru.magnit.magreportbackend.dto.response.olap.OlapCubeResponse;
import ru.magnit.magreportbackend.dto.response.reportjob.ReportJobMetadataResponse;
import ru.magnit.magreportbackend.service.domain.converter.Reader;
import ru.magnit.magreportbackend.service.domain.converter.Writer;
import ru.magnit.magreportbackend.service.domain.converter.WriterFactory;
import ru.magnit.magreportbackend.service.telemetry.TelemetryService;

import java.nio.file.Path;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WriterFactoryImpl implements WriterFactory {

    @Value("${magreport.excel-template.nameDataList}")
    private String nameDataList;
    private final TelemetryService telemetryService;

    @Override
    public Writer createWriter(Reader reader, ReportData data, Path exportPath) {
        return new ExcelWriter(reader, data, exportPath, nameDataList, telemetryService);
    }

    @Override
    public Writer createWriter(OlapCubeResponse data, ReportJobMetadataResponse metadata, Map<String, Object> config, OlapExportPivotTableRequest request, Path exportPath) {
        return new PivotTableWriter(data, request, metadata, config, telemetryService, exportPath, nameDataList);
    }
}
