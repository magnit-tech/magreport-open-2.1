package ru.magnit.magreportbackend.service.domain.log_reader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.magnit.magreportbackend.dto.inner.olap.OlapUserRequestLog;
import ru.magnit.magreportbackend.dto.request.DatePeriodRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPInputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogReaderService {

    private static final String ERROR_PARSE_LOG = "Error trying to parse log:";
    @Value("${logging.olap.file.name}")
    private String logPath;

    private final Map<Path, DatePeriod> logMetaCache = new HashMap<>();

    private final ObjectMapper objectMapper;

    @EventListener
    @SuppressWarnings("unused")
    public void onApplicationEvent(ContextRefreshedEvent event) {
        refreshLogMetaCache();
    }

    public synchronized List<OlapUserRequestLog> getLogEntriesForPeriod(DatePeriodRequest periodRequest) {
        logMetaCache.remove(Paths.get(logPath));
        refreshLogMetaCache();
        return logMetaCache.entrySet()
            .stream()
            .filter(entry -> isIntersects(entry.getValue(), periodRequest))
            .flatMap(entry -> getLogEntriesInPeriod(entry.getKey(), periodRequest).stream())
            .toList();
    }

    private boolean isIntersects(DatePeriod firstPeriod, DatePeriodRequest secondPeriod) {
        if (firstPeriod.startDateTime() == null || firstPeriod.endDateTime() == null) return false;

        return secondPeriod.getStartDate().isBefore(firstPeriod.endDateTime())
            && secondPeriod.getEndDate().isAfter(firstPeriod.startDateTime());
    }

    private boolean isInPeriod(LocalDateTime dateTime, DatePeriodRequest period) {
        return dateTime.isEqual(period.getStartDate()) ||
            dateTime.isEqual(period.getEndDate()) ||
            (dateTime.isAfter(period.getStartDate()) && dateTime.isBefore(period.getEndDate()));
    }

    private List<OlapUserRequestLog> getLogEntriesInPeriod(Path logPath, DatePeriodRequest period) {
        if (!getFileExtension(logPath).equalsIgnoreCase("log")) {
            return getEntriesFromZipLog(logPath, period);
        } else {
            return getEntriesFromLog(logPath, period);
        }
    }

    private void refreshLogMetaCache() {
        final var path = Paths.get(logPath);
        final var logFilenameWithExtension = path.getFileName().toString();
        final var logFilename = logFilenameWithExtension.split("\\.")[0];
        try (final var fileStream = Files.list(path.getParent())) {
            fileStream
                .filter(file -> !Files.isDirectory(file) && file.getFileName().toString().startsWith(logFilename))
                .forEach(file -> {
                    if (!logMetaCache.containsKey(file)) {
                        logMetaCache.put(file, getPeriod(file));
                    }
                });
        } catch (Exception ex) {
            log.error("Error trying to refresh metadata of OLAP log files", ex);
        }
    }

    private DatePeriod getPeriod(Path fileName) {
        final var startDateTime = new AtomicReference<LocalDateTime>(null);
        final var endDateTime = new AtomicReference<LocalDateTime>(null);
        if (getFileExtension(fileName).equalsIgnoreCase("log")) {
            processCommonFile(fileName, startDateTime, endDateTime);
        } else {
            processZipFile(fileName, startDateTime, endDateTime);
        }
        return new DatePeriod(startDateTime.get(), endDateTime.get());
    }

    private void processZipFile(Path fileName, AtomicReference<LocalDateTime> startDateTime, AtomicReference<LocalDateTime> endDateTime) {
        try (final var zipInputStream = new GZIPInputStream(Files.newInputStream(fileName))) {
            final var logLines = new BufferedReader(new InputStreamReader(zipInputStream)).lines();
            logLines.forEach(line -> {
                try {
                    final var requestLog = objectMapper.readValue(line, OlapUserRequestLog.class);
                    if (startDateTime.get() == null) startDateTime.set(requestLog.getDateTime());
                    endDateTime.set(requestLog.getDateTime());
                } catch (JsonProcessingException e) {
                    log.error(ERROR_PARSE_LOG + fileName, e);
                }
            });
        } catch (Exception ex) {
            log.error("Failed to open file:" + fileName, ex);
        }
    }

    private void processCommonFile(Path fileName, AtomicReference<LocalDateTime> startDateTime, AtomicReference<LocalDateTime> endDateTime) {
        try (final var logLines = Files.lines(fileName)) {
            logLines.forEach(line -> {
                try {
                    final var requestLog = objectMapper.readValue(line, OlapUserRequestLog.class);
                    if (startDateTime.get() == null) startDateTime.set(requestLog.getDateTime());
                    endDateTime.set(requestLog.getDateTime());
                } catch (JsonProcessingException e) {
                    log.error(ERROR_PARSE_LOG + fileName, e);
                }
            });
        } catch (Exception ex) {
            log.error("Error trying read file: " + fileName, ex);
        }
    }

    private List<OlapUserRequestLog> getEntriesFromZipLog(Path filePath, DatePeriodRequest period) {
        final var result = new ArrayList<OlapUserRequestLog>();
        try (final var zipInputStream = new GZIPInputStream(Files.newInputStream(filePath))) {
            final var logLines = new BufferedReader(new InputStreamReader(zipInputStream)).lines();
            logLines.forEach(line -> {
                try {
                    final var logEntry = objectMapper.readValue(line, OlapUserRequestLog.class);
                    if (isInPeriod(logEntry.getDateTime(), period)) result.add(logEntry);
                } catch (JsonProcessingException e) {
                    log.error(ERROR_PARSE_LOG + filePath, e);
                }
            });
        } catch (Exception ex) {
            log.error("Failed to open file:" + filePath, ex);
        }
        return result;
    }

    private List<OlapUserRequestLog> getEntriesFromLog(Path filePath, DatePeriodRequest period) {
        final var result = new ArrayList<OlapUserRequestLog>();
        try (final var logLines = Files.lines(filePath)) {
            logLines.forEach(line -> {
                try {
                    final var logEntry = objectMapper.readValue(line, OlapUserRequestLog.class);
                    if (isInPeriod(logEntry.getDateTime(), period)) result.add(logEntry);
                } catch (JsonProcessingException e) {
                    log.error(ERROR_PARSE_LOG + filePath, e);
                }
            });
        } catch (Exception ex) {
            log.error("Error trying read file: " + filePath, ex);
        }
        return result;
    }

    private String getFileExtension(Path fileName) {
        final var fileParts = fileName.getFileName().toString().split("\\.");
        return fileParts[fileParts.length - 1];
    }
}
