package ru.magnit.magreportbackend.dto.response.reportjob;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

public record ReportJobHistoryResponse(
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    Long reportJobId,

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    List<ReportJobStatisticsResponse> history
) {
}
