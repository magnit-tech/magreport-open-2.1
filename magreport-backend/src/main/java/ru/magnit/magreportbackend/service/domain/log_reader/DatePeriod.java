package ru.magnit.magreportbackend.service.domain.log_reader;

import java.time.LocalDateTime;
import java.util.Objects;

public record DatePeriod(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatePeriod that = (DatePeriod) o;

        if (!Objects.equals(startDateTime, that.startDateTime))
            return false;
        return Objects.equals(endDateTime, that.endDateTime);
    }

}
