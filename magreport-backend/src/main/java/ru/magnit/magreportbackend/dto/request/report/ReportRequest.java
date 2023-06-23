package ru.magnit.magreportbackend.dto.request.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.lang.NonNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class ReportRequest {
    @NonNull
    private Long id;
    private Long jobId;
}
