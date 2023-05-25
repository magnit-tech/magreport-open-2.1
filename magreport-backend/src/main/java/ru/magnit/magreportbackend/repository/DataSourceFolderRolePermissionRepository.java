package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.datasource.DataSourceFolderRolePermission;

public interface DataSourceFolderRolePermissionRepository extends JpaRepository<DataSourceFolderRolePermission, Long> {

    void deleteAllByFolderRoleId(Long folderRoleId);

}
