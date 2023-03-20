package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.folderreport.FolderRolePermission;

public interface FolderRolePermissionRepository extends JpaRepository<FolderRolePermission, Long > {

    void deleteAllByFolderRoleId(Long folderRoleId);

}
