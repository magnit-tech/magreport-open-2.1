package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTemplateFolderRolePermission;

public interface FilterTemplateFolderRolePermissionRepository extends JpaRepository<FilterTemplateFolderRolePermission, Long> {
}
