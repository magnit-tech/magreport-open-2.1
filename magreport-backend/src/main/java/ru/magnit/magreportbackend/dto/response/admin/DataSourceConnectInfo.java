package ru.magnit.magreportbackend.dto.response.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class DataSourceConnectInfo {

    private Long dataSourceId;
    private String dataSourceName;
    private int connectPoolSize;
    private int activeConnectSize;
    private int queueConnectSize;

}
