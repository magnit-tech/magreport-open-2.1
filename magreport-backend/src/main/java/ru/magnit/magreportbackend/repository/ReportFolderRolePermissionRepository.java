package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.report.ReportFolderRolePermission;

public interface ReportFolderRolePermissionRepository extends JpaRepository<ReportFolderRolePermission, Long> {

    void deleteAllByFolderRoleId(Long folderRoleId);

}
