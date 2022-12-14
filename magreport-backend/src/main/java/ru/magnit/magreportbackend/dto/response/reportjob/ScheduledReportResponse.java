package ru.magnit.magreportbackend.dto.response.reportjob;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ScheduledReportResponse(
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    Long id,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    String name
) {
}
