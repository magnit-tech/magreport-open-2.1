package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceFolderRolePermission;

public interface FilterInstanceFolderRolePermissionRepository extends JpaRepository<FilterInstanceFolderRolePermission, Long> {
    void deleteAllByFolderRoleId(Long folderRoleId);
}
