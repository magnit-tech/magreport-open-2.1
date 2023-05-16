package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterFolderRolePermission;

public interface SecurityFilterFolderRolePermissionRepository extends JpaRepository<SecurityFilterFolderRolePermission, Long> {
    void deleteAllByFolderRoleId(Long folderRoleId);
}
