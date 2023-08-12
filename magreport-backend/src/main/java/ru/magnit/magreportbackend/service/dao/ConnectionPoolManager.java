package ru.magnit.magreportbackend.service.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.datasource.DataSourceTypeEnum;
import ru.magnit.magreportbackend.dto.inner.datasource.DataSourceData;
import ru.magnit.magreportbackend.dto.response.admin.DataSourceConnectInfo;
import ru.magnit.magreportbackend.service.security.CryptoService;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionPoolManager {
    private final Map<DataSourceData, DataSource> dataSources = new ConcurrentHashMap<>();
    private final CryptoService cryptoService;

    public Connection getConnection(DataSourceData dataSource) throws SQLException {
        return dataSources.computeIfAbsent(dataSource, this::getHikariDataSource).getConnection();
    }

    public boolean isConnectionAvailable(DataSourceData dataSource) {
        try {
            final var source = (HikariDataSource) dataSources.computeIfAbsent(dataSource, this::getHikariDataSource);
            final var maxPoolSize = source.getMaximumPoolSize();
            final var activeConnections = source.getHikariPoolMXBean().getActiveConnections();
            return maxPoolSize - activeConnections > 0;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return true;
        }
    }

    private DataSource getHikariDataSource(DataSourceData dataSource) {
        log.debug("Creating new DataSource with id:" + dataSource.id() + " and url: " + dataSource.url());
        return new HikariDataSource(getHikariConfig(dataSource));
    }

    public void deleteDataSource(DataSourceData dataSource) {
        if (dataSources.containsKey(dataSource)) {
            log.debug("Deleting datasource with id:" + dataSource.id() + " and url:" + dataSource.url());
            final var deletedDataSource = dataSources.get(dataSource);
            try {
                deletedDataSource.unwrap(HikariDataSource.class).close();
                dataSources.remove(dataSource);
            } catch (SQLException ex) {
                log.error("ConnectionManager.deleteDataSource(): Error trying to close JDBC connection pool", ex);
            }
        }
    }

    private HikariConfig getHikariConfig(DataSourceData dataSource) {
        final var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(dataSource.url());
        hikariConfig.setUsername(dataSource.userName());
        hikariConfig.setPassword(cryptoService.decode(dataSource.password()));
        hikariConfig.setReadOnly(dataSource.type() == DataSourceTypeEnum.CLICK_HOUSE);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setLeakDetectionThreshold(1800000);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMinimumIdle(3);
        hikariConfig.setMaximumPoolSize(dataSource.poolSize());
        hikariConfig.setConnectionTimeout(10000);
        return hikariConfig;
    }


    public List<DataSourceConnectInfo> getDataSourcePoolInfo() {

        var responses = new ArrayList<DataSourceConnectInfo>();

        dataSources.forEach((key, value) -> {

            var response = new DataSourceConnectInfo();
            var datasource = (HikariDataSource) value;

            response
                    .setDataSourceId(key.id())
                    .setConnectPoolSize(datasource.getMaximumPoolSize())
                    .setActiveConnectSize(datasource.getHikariPoolMXBean().getActiveConnections())
                    .setQueueConnectSize(datasource.getHikariPoolMXBean().getThreadsAwaitingConnection());

            responses.add(response);
        });

        return responses;
    }

    @PreDestroy
    private void shutdown() {
        dataSources.values().forEach(dataSource -> {
            try {
                dataSource.unwrap(HikariDataSource.class).close();
            } catch (SQLException ex) {
                log.error("ConnectionManager.unInit(): Error trying to close JDBC connection pool", ex);
            }
        });
    }
}
