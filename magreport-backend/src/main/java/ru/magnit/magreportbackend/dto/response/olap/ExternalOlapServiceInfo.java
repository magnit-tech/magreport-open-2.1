package ru.magnit.magreportbackend.dto.response.olap;

import java.time.LocalDateTime;

public record ExternalOlapServiceInfo(
    String url,
    float refreshInterval,
    LocalDateTime lastRefreshTime,
    float secondsFromLastRefresh
) {
}
