package ru.magnit.magreportbackend.dto.response.data_governance;

import ru.magnit.magreportbackend.dto.backup.dataset.DataSetBackupTuple;
import ru.magnit.magreportbackend.dto.backup.dataset.DataSetFieldBackupTuple;
import ru.magnit.magreportbackend.dto.backup.dataset.DataTypeBackupTuple;
import ru.magnit.magreportbackend.dto.backup.datasource.DataSourceBackupTuple;
import ru.magnit.magreportbackend.dto.backup.datasource.DataSourceTypeBackupTuple;
import ru.magnit.magreportbackend.dto.backup.folder.FolderBackupTuple;
import ru.magnit.magreportbackend.dto.backup.folder.FolderReportBackupTuple;
import ru.magnit.magreportbackend.dto.backup.report.ReportBackupTuple;
import ru.magnit.magreportbackend.dto.backup.report.ReportFieldBackupTuple;
import ru.magnit.magreportbackend.dto.backup.report.ReportFolderBackupTuple;
import ru.magnit.magreportbackend.dto.backup.user.UserTuple;
import ru.magnit.magreportbackend.dto.response.dataset.DataSetTypeResponse;
import ru.magnit.magreportbackend.dto.response.report.PivotFieldTypeResponse;

import java.util.List;

public record DataGovernanceResponse(
    List<DataTypeBackupTuple> dataTypes,
    List<PivotFieldTypeResponse> pivotTypes,
    List<DataSourceTypeBackupTuple> dataSourceTypes,
    List<DataSetTypeResponse> dataSetTypes,
    List<DataSourceBackupTuple> dataSources,
    List<DataSetBackupTuple> dataSets,
    List<DataSetFieldBackupTuple> dataSetFields,
    List<ReportBackupTuple> reports,
    List<ReportFieldBackupTuple> reportFields,
    List<ReportFolderBackupTuple> devReportFolders,
    List<FolderReportBackupTuple> reportFolderLinks,
    List<FolderBackupTuple> reportFolders,
    List<UserTuple> users
    ) {
}
