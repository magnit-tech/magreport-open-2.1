package ru.magnit.magreportbackend.mapper.reportjob;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobUser;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobUserTypeEnum;
import ru.magnit.magreportbackend.dto.response.reportjob.ReportJobUserResponse;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportJobUserResponseMapper implements Mapper<ReportJobUserResponse, ReportJobUser > {

    @Override
    public ReportJobUserResponse from(ReportJobUser source) {
        return new ReportJobUserResponse()
                .setUserName(source.getUser().getName())
                .setType(ReportJobUserTypeEnum.getById(source.getType().getId()));
    }
}
