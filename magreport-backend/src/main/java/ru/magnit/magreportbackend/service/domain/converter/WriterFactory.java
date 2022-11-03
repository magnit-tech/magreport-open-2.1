package ru.magnit.magreportbackend.service.domain.converter;

import ru.magnit.magreportbackend.dto.inner.reportjob.ReportData;
import ru.magnit.magreportbackend.dto.request.olap.OlapExportPivotTableRequest;
import ru.magnit.magreportbackend.dto.response.olap.OlapCubeResponse;
import ru.magnit.magreportbackend.dto.response.reportjob.ReportJobMetadataResponse;

import java.nio.file.Path;
import java.util.Map;


public interface WriterFactory {

    Writer createWriter(Reader reader, ReportData data, Path exportPath);
    Writer createWriter (OlapCubeResponse data, ReportJobMetadataResponse metadata, Map<String,Object> config, OlapExportPivotTableRequest request, Path exportPath);
}
