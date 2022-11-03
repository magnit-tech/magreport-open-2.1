package ru.magnit.magreportbackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.magnit.magreportbackend.dto.request.olap.OlapServiceInfo;
import ru.magnit.magreportbackend.dto.request.olap.OlapServiceRegisterInfo;
import ru.magnit.magreportbackend.dto.response.olap.ExternalOlapServiceInfo;
import ru.magnit.magreportbackend.exception.OlapExternalServiceException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ExternalOlapManager {
    @Value("${magreport.olap.out-service.get-cube-url}")
    String getCubeUrl;
    @Value("${magreport.olap.out-service.get-fields-value-url}")
    String getFieldsValueUrl;

    private final Map<String, OlapServiceInfo> olapServices = new ConcurrentHashMap<>();
    private final Map<Long, String> jobServices = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> serviceRequests = new ConcurrentHashMap<>();

    public void registerExternalService(OlapServiceRegisterInfo request) {
        olapServices.put(request.getUrl(), new OlapServiceInfo(request.getUrl(), request.getRefreshInterval(), LocalDateTime.now()));
    }

    public List<ExternalOlapServiceInfo> getExternalServices() {
        return olapServices.entrySet()
            .stream()
            .map(entry -> new ExternalOlapServiceInfo(
                entry.getKey(),
                entry.getValue().refreshInterval(),
                entry.getValue().lastRefreshed(),
                ChronoUnit.MILLIS.between(entry.getValue().lastRefreshed(), LocalDateTime.now()) / 1000f
            ))
            .toList();
    }

    public String getExternalServiceCubeURL(Long jobId) {
        return getUrl(jobId, getCubeUrl);
    }

    public String getExternalServiceFieldsURL(Long jobId) {
        return getUrl(jobId, getFieldsValueUrl);
    }

    public void onRequestComplete(Long jobId) {
        serviceRequests.get(jobServices.get(jobId)).decrementAndGet();
    }

    private String getUrl(Long jobId, String servicePath) {
        if (jobServices.containsKey(jobId)) {
            final var service = jobServices.get(jobId);
            serviceRequests.get(service).incrementAndGet();
            return olapServices.get(service).url() + servicePath;
        }

        final var minUsedService = getMinUsedService();

        jobServices.putIfAbsent(jobId, minUsedService);
        serviceRequests.putIfAbsent(minUsedService, new AtomicLong(1L));

        return olapServices.get(minUsedService).url() + servicePath;
    }

    private String getMinUsedService() {
        String minUsedService;
        if (!serviceRequests.isEmpty()) {
            minUsedService = serviceRequests.entrySet()
                .stream()
                .min(Comparator.comparingLong(a -> a.getValue().get()))
                .map(Map.Entry::getKey)
                .orElseThrow();
        } else if (olapServices.isEmpty()) {
            throw new OlapExternalServiceException("Внешние OLAP сервисы не зарегистрированы.");
        } else {
            minUsedService = olapServices.keySet().toArray()[0].toString();
        }
        return minUsedService;
    }
}
