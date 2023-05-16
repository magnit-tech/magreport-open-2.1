package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.excel.ExcelTemplateFolderRolePermission;

public interface ExcelTemplateFolderRolePermissionRepository extends JpaRepository<ExcelTemplateFolderRolePermission, Long> {
    void deleteAllByFolderRoleId(Long folderRoleId);
}
