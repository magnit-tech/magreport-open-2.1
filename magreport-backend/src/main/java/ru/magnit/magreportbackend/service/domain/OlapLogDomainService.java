package ru.magnit.magreportbackend.service.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.inner.olap.OlapUserRequestLog;
import ru.magnit.magreportbackend.dto.request.DatePeriodRequest;
import ru.magnit.magreportbackend.service.domain.log_reader.LogReaderService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OlapLogDomainService {

    private final LogReaderService logReaderService;

    public List<OlapUserRequestLog> getLogEntriesForPeriod(DatePeriodRequest periodRequest) {
        return logReaderService.getLogEntriesForPeriod(periodRequest);
    }
}
