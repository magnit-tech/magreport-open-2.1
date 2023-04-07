package ru.magnit.magreportbackend.service.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import ru.magnit.magreportbackend.dto.inner.olap.CubeData;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportJobData;
import ru.magnit.magreportbackend.dto.response.reportjob.ReportPageResponse;
import ru.magnit.magreportbackend.exception.ReportExportException;
import ru.magnit.magreportbackend.service.domain.converter.ReaderFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ru.magnit.magreportbackend.util.FileUtils.replaceHomeShortcut;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvroReportDomainService {

    private final ReaderFactory readerFactory;
    private final ObjectMapper objectMapper;

    private static final int BUFFER_CAPACITY = 65_536;
    private static final int LOG_ROW_INTERVAL = 100_000;
    private static final int BATCH_SIZE = 100;

    @Value("${magreport.reports.folder}")
    private String reportFolder;

    public ReportPageResponse getPage(ReportJobData jobData, Long pageNumber, Long rowsPerPage) {
        List<Map<String, String>> resultRows = new ArrayList<>();

        try (var reader = readerFactory.createReader(jobData, getPath(jobData))) {
            long rowsToSkip = (pageNumber - 1) * rowsPerPage;
            long rowsToRead = rowsPerPage;
            while (rowsToSkip-- > 0) reader.getRow();
            while (rowsToRead-- > 0) {
                var cacheRow = reader.getRow();
                if (cacheRow == null) break;
                var row = new LinkedHashMap<String, String>(cacheRow.entries().size());
                cacheRow.entries().forEach(entry -> row.put(entry.fieldData().name(), entry.value()));
                resultRows.add(row);
            }
        } catch (Exception ex) {
            throw new ReportExportException("Error while trying to get report page", ex);
        }

        return new ReportPageResponse(
                jobData.reportId(),
                jobData.id(),
                pageNumber,
                jobData.rowCount(),
                resultRows);
    }

    @SuppressWarnings("java:S125")
    public CubeData getCubeData(ReportJobData jobData) {
        try (var reader = readerFactory.createReader(jobData, getPath(jobData))) {
            log.debug("Cube for jobId: " + jobData.id() + " loaded into cache.");
            return reader.getCube();
        } catch (Exception ex) {
            throw new ReportExportException("Error while trying to read report in memory", ex);
        }
    }

    private Path getPath(ReportJobData jobData) {
        return Paths.get(replaceHomeShortcut(reportFolder) + "/" + jobData.id() + ".avro");
    }

    public void streamReport(ResponseBodyEmitter emitter, ReportJobData jobData) {
        var firstRow = true;
        var rowCount = 0;
        try (var reader = readerFactory.createReader(jobData, getPath(jobData))) {
            log.debug("Start data streaming for job with id: " + jobData.id());
            var emitBuffer = new StringBuilder(BUFFER_CAPACITY);
            emitBuffer.append("[");
            while (true) {
                var cacheRow = reader.getRow();
                if (cacheRow == null) break;
                if (!firstRow) emitBuffer.append(",");
                final var rowMap = new LinkedHashMap<Integer, String>(cacheRow.entries().size());

                for (final var entry : cacheRow.entries()) {
                    rowMap.put(entry.fieldData().ordinal(), entry.value());
                }
                emitBuffer.append(objectMapper.writeValueAsString(rowMap));
                rowCount++;
                if (rowCount % LOG_ROW_INTERVAL == 0)
                    log.debug(rowCount + " rows streamed out of " + jobData.rowCount() + " for job with id: " + jobData.id());
                if (rowCount % BATCH_SIZE == 0) {
                    emitter.send(emitBuffer.toString());
                    emitBuffer = new StringBuilder(BUFFER_CAPACITY);
                }
                firstRow = false;
            }
            emitBuffer.append("]");
            emitter.send(emitBuffer.toString());
            log.debug(rowCount + " rows streamed out of " + jobData.rowCount() + " for job with id: " + jobData.id());
        } catch (Exception ex) {
            log.error("Error while trying to stream report data", ex);
            throw new ReportExportException("Error while trying to stream report data", ex);
        }
    }
}
