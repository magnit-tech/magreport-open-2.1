package ru.magnit.magreportbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.backup.BackupRequest;
import ru.magnit.magreportbackend.service.domain.AdminDomainService;
import ru.magnit.magreportbackend.service.domain.BackupService;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminDomainService domainService;
    private final BackupService backupService;

    public byte[] getMainActiveLog() {
        return domainService.getMainLog();
    }

    public byte[] getOlapActiveLog() {
        return domainService.getOlapLog();
    }

    public byte[] createBackup(BackupRequest request)  {
     return backupService.createBackup(request);
    }
}
