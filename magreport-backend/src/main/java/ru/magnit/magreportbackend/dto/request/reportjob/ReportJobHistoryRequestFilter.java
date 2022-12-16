package ru.magnit.magreportbackend.dto.request.reportjob;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class ReportJobHistoryRequestFilter {
    private LocalDateTime from;
    private LocalDateTime to;
    private List<ReportJobStatusEnum> statuses;
    private List<Long> users;
    private List<Long> reportIds;
}
