package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.folderreport.FolderAuthority;

public interface FolderAuthorityRepository extends JpaRepository<FolderAuthority, Long > {
}
