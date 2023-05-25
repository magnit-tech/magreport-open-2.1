package ru.magnit.magreportbackend.service.domain.converter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.inner.olap.ExportPivotConfiguration;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportData;
import ru.magnit.magreportbackend.service.domain.converter.Reader;
import ru.magnit.magreportbackend.service.domain.converter.Writer;
import ru.magnit.magreportbackend.service.domain.converter.WriterFactory;
import ru.magnit.magreportbackend.service.telemetry.TelemetryService;

import java.nio.file.Path;

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
    public Writer createWriter(ExportPivotConfiguration configuration, Path exportPath) {
        return new PivotTableWriter(configuration, telemetryService, exportPath, nameDataList);
    }
}
