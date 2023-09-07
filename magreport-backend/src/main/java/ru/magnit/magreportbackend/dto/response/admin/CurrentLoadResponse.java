package ru.magnit.magreportbackend.dto.response.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class CurrentLoadResponse {
    private int totalJobCountThreads;
    private int countJobWorkingThreads;
    private int totalExportCountThreads;
    private int countExportCountThreads;
    List<DataSourceConnectInfo> dataSourceConnectInfo;
}
