package ru.magnit.magreportbackend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.magnit.magreportbackend.dto.inner.olap.OlapUserRequestLog;

@Slf4j
@UtilityClass
public class LogHelper {
    private final String IN_MESSAGE = "IN: ";
    private final String OUT_MESSAGE = "OUT: ";
    private static final Logger olapRequestLog = LoggerFactory.getLogger("ru.magnit.magreportbackend.olap_user_request_logger");


    public void logInfoUserMethodStart() {
        if (!log.isInfoEnabled()) return;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(IN_MESSAGE + getCurrentMethodName() + ", user: " + authentication.getName());
    }

    public void logInfoUserMethodEnd() {
        if (!log.isInfoEnabled()) return;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(OUT_MESSAGE + getCurrentMethodName() + ", user: " + authentication.getName());
    }

    public void logInfoOlapUserRequest(ObjectMapper objectMapper, OlapUserRequestLog request) throws JsonProcessingException {
        var message = objectMapper.writeValueAsString(request);
        olapRequestLog.debug(message);

    }

    private String getCurrentMethodName() {
        return new Throwable().getStackTrace()[2].getMethodName();
    }
}
