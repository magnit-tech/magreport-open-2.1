package ru.magnit.magreportbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.magnit.magreportbackend.dto.backup.BackupRequest;
import ru.magnit.magreportbackend.dto.backup.BackupRestoreRequest;
import ru.magnit.magreportbackend.dto.response.data_governance.DataGovernanceResponse;
import ru.magnit.magreportbackend.service.domain.AdminDomainService;
import ru.magnit.magreportbackend.service.domain.BackupService;
import ru.magnit.magreportbackend.service.domain.data_lineage.DataLineageService;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminDomainService domainService;
    private final BackupService backupService;
    private final DataLineageService dataLineageService;

    public byte[] getMainActiveLog() {
        return domainService.getMainLog();
    }

    public byte[] getOlapActiveLog() {
        return domainService.getOlapLog();
    }

    public byte[] createBackup(BackupRequest request) {
        return backupService.createBackup(request);
    }

    public void loadBackup(BackupRestoreRequest request, MultipartFile backup) {
        backupService.restoreBackup(request, backup);
    }

    public DataGovernanceResponse getDataLineage() {
        return new DataGovernanceResponse(
                dataLineageService.getAllDataTypes(),
                dataLineageService.getAllPivotTypes(),
                dataLineageService.getAllDataSourceTypes(),
                dataLineageService.getAllDatasetTypes(),
                dataLineageService.getAllDataSources(),
                dataLineageService.getAllDataSets(),
                dataLineageService.getAllDataSetFields(),
                dataLineageService.getAllReports(),
                dataLineageService.getAllReportFields(),
                dataLineageService.getAllDevReportFolders(),
                dataLineageService.getAllUserReportFolderLinks(),
                dataLineageService.getAllUserReportFolders(),
                dataLineageService.getAllUsers()
        );
    }
}
