package ru.magnit.magreportbackend.dto.response.reportjob;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobUserTypeEnum;
@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
public class ReportJobUserResponse  {

    private String userName;
    private ReportJobUserTypeEnum type;

}
