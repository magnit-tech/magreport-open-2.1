package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.magnit.magreportbackend.domain.dataset.DataSetFolderRolePermission;

import java.util.List;

public interface DataSetFolderRolePermissionRepository extends JpaRepository<DataSetFolderRolePermission, Long> {
}
