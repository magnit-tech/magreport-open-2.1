package ru.magnit.magreportbackend.dto.request.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class EventPivotRegisterRequest {

    private Long reportId;
    private Long jobId;
    private String event;

}
