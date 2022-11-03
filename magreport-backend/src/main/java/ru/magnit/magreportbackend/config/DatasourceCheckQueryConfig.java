package ru.magnit.magreportbackend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.magnit.magreportbackend.domain.datasource.DataSourceTypeEnum;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "magreport.datasource")
public class DatasourceCheckQueryConfig {
    private Map<DataSourceTypeEnum, String> checkRequests;

    public String getQueryForDatasourceType(DataSourceTypeEnum type) {
        return checkRequests.get(type);
    }
}
