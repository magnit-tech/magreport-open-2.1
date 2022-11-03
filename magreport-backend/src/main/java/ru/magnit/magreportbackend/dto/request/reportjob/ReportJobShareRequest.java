package ru.magnit.magreportbackend.dto.request.reportjob;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.dto.request.user.UserRequest;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class ReportJobShareRequest {

    private Long jobId;
    private List<UserRequest> users = Collections.emptyList();

}
