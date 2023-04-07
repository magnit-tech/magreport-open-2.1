package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.dataset.DataSetFolderRolePermission;

public interface DataSetFolderRolePermissionRepository extends JpaRepository<DataSetFolderRolePermission, Long> {

    void deleteAllByFolderRoleId(Long folderRoleId);

}
