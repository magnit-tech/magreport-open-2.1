package ru.magnit.magreportbackend.service.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.inner.olap.CubeData;
import ru.magnit.magreportbackend.dto.inner.olap.MetricResult;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportJobData;
import ru.magnit.magreportbackend.dto.request.olap.FilterGroup;
import ru.magnit.magreportbackend.dto.request.olap.MetricFilterGroup;
import ru.magnit.magreportbackend.dto.response.olap.OlapInfoCubesResponse;
import ru.magnit.magreportbackend.service.domain.filter.CubeFilters;
import ru.magnit.magreportbackend.service.domain.metricsfilter.MetricFilters;
import ru.magnit.magreportbackend.util.Triple;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class OlapDomainService {

    @Value("${magreport.olap.max-cache-size}")
    private int maxCacheSize;

    private final Map<ReportJobData, CubeData> cubeCache = new ConcurrentHashMap<>();
    private final Map<ReportJobData, LocalDateTime> startUseTimes = new ConcurrentHashMap<>();
    private final Map<ReportJobData, LocalDateTime> lastUseTimes = new ConcurrentHashMap<>();
    private final Map<ReportJobData, Long> useCounts = new ConcurrentHashMap<>();
    private final AvroReportDomainService avroReportDomainService;

    public CubeData getCubeData(ReportJobData jobData) {
        var result = cubeCache.computeIfAbsent(jobData, avroReportDomainService::getCubeData);
        startUseTimes.computeIfAbsent(jobData, o -> LocalDateTime.now());
        lastUseTimes.put(jobData, LocalDateTime.now());
        useCounts.put(jobData, useCounts.getOrDefault(jobData, 0L) + 1L);

        dropOldestEntryFromCache();

        return result;
    }

    public List<OlapInfoCubesResponse> getInfoAboutCubes() {

        return cubeCache
                .keySet()
                .stream()
                .map(jobData ->
                        new OlapInfoCubesResponse(
                                jobData.reportId(),
                                jobData.reportData().name(),
                                jobData.id(),
                                jobData.rowCount(),
                                jobData.userName(),
                                useCounts.get(jobData),
                                startUseTimes.get(jobData),
                                lastUseTimes.get(jobData)
                        )
                )
                .toList();

    }


    private synchronized void dropOldestEntryFromCache() {
        if (lastUseTimes.keySet().size() <= maxCacheSize) return;

        var oldestEntry =
                lastUseTimes.entrySet().stream().min(Map.Entry.comparingByValue());

        oldestEntry.ifPresent(entry -> {
            log.debug("Cube for jobId " + entry.getKey().id() + "is dropped from cache.");
            cubeCache.remove(entry.getKey());
            startUseTimes.remove(entry.getKey());
            useCounts.remove(entry.getKey());
            lastUseTimes.remove(entry.getKey());
        });
    }

    public boolean[] filterCubeData(CubeData sourceCube, FilterGroup filterGroup) {
        final var cubeFilter = CubeFilters.createFilter(sourceCube, filterGroup);
        final var checkRows = new boolean[sourceCube.numRows()];

        for (int row = 0; row < sourceCube.numRows(); row++)
            checkRows[row] = cubeFilter.filter(row);

        return checkRows;
    }

    public Triple<boolean[][], boolean[], boolean[]> filterMetricResult(MetricResult[][][] metrics, MetricFilterGroup filterGroup, List<DataTypeEnum> dataTypesMetrics) {

        var metricFilter = MetricFilters.createFilter(metrics, filterGroup, dataTypesMetrics);

        var result = new boolean[metrics.length][metrics[0].length];
        var filteredColumns = new boolean[metrics.length];
        var filteredRows = new boolean[metrics[0].length];

        Arrays.fill(filteredColumns, true);
        Arrays.fill(filteredRows, true);

        for (int column = 0; column < metrics.length; column++)
            for (int row = 0; row < metrics[0].length; row++) {

                var filterValue = metricFilter.filter(column, row);
                result[column][row] = filterValue;
                filteredColumns[column] &= !filterValue;
                filteredRows[row] &= !filterValue;
            }

        return new Triple<>(result, filteredColumns, filteredRows);

    }

}
