package ru.magnit.magreportbackend.dto.request.olap;

import java.time.LocalDateTime;

public record OlapServiceInfo(
    String url,
    float refreshInterval,
    LocalDateTime lastRefreshed
) {
}
