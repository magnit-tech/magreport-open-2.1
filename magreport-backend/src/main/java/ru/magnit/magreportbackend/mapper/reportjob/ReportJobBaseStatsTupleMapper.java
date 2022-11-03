package ru.magnit.magreportbackend.mapper.reportjob;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.response.reportjob.ReportJobBaseStats;
import ru.magnit.magreportbackend.mapper.Mapper;

import javax.persistence.Tuple;

@Service
@RequiredArgsConstructor
public class ReportJobBaseStatsTupleMapper implements Mapper<ReportJobBaseStats, Tuple> {

    @Override
    public ReportJobBaseStats from(Tuple source) {
        return new ReportJobBaseStats(
            source.get("REPORT_JOB_ID", Integer.class),
            source.get("REPORT_ID", Integer.class),
            source.get("reportName", String.class),
            source.get("userName", String.class)
        );
    }
}
