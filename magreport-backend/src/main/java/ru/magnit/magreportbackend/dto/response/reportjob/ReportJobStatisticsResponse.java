package ru.magnit.magreportbackend.dto.response.reportjob;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static ru.magnit.magreportbackend.util.Constant.ISO_DATE_TIME_PATTERN;

public record ReportJobStatisticsResponse(
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    String status,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    String state,
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    Long rowCount,
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    Long exportExcelCount,
    @JsonFormat(shape = JsonFormat.Shape.BOOLEAN)
    Boolean isShare,
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    Long olapRequestCount,
    @JsonFormat(pattern = ISO_DATE_TIME_PATTERN)
    LocalDateTime created
    ) {
}
