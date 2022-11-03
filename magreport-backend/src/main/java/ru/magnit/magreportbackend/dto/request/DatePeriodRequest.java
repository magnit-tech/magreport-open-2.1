package ru.magnit.magreportbackend.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
public class DatePeriodRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
