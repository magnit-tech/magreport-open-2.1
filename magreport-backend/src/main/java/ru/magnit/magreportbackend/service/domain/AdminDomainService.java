package ru.magnit.magreportbackend.service.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.response.admin.CurrentLoadResponse;
import ru.magnit.magreportbackend.exception.FileSystemException;
import ru.magnit.magreportbackend.service.dao.ConnectionPoolManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminDomainService {

    @Value("${logging.magreport.file.name}")
    private String mainLogPath;

    @Value("${logging.olap.file.name}")
    private String olapLogPath;

    private final ConnectionPoolManager poolManager;

    @Qualifier("JobEngineTaskExecutor")
    private final ThreadPoolTaskExecutor taskExecutor;

    @Qualifier("JobEngineTaskExport")
    private final ThreadPoolTaskExecutor taskExport;


    public byte[] getMainLog() {
        return getActiveLog(mainLogPath);
    }

    public byte[] getOlapLog() {
        return getActiveLog(olapLogPath);
    }

    public CurrentLoadResponse getCurrentLoad() {

        var response = new CurrentLoadResponse();

        response.setTotalJobCountThreads(taskExecutor.getMaxPoolSize());
        response.setCountJobWorkingThreads(taskExecutor.getActiveCount());

        response.setTotalExportCountThreads(taskExecutor.getMaxPoolSize());
        response.setCountExportCountThreads(taskExport.getActiveCount());

        response.setDataSourceConnectInfo(poolManager.getDataSourcePoolInfo());

        return response;
    }


    private byte[] getActiveLog(String logPath) {

        try (
                InputStream in = Files.newInputStream(Paths.get(logPath))
        ) {
            log.debug("Log path: " + logPath);
            return IOUtils.toByteArray(in);
        } catch (IOException ex) {
            log.error("Error trying to get log file", ex);
            throw new FileSystemException("Error trying to get log file", ex);
        }
    }
}
