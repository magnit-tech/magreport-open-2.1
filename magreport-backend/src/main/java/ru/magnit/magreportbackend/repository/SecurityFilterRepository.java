package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilter;

import java.util.Collection;
import java.util.List;

public interface SecurityFilterRepository extends JpaRepository<SecurityFilter, Long> {
    Boolean existsByFolderId(Long id);

    List<SecurityFilter> getAllByIdIn(List<Long> ids);

    @Query(value = """
        SELECT DISTINCT SF.* FROM REPOSITORY.SECURITY_FILTER SF
        INNER JOIN REPOSITORY.SECURITY_FILTER_DATASET SFD ON SFD.SECURITY_FILTER_ID = SF.SECURITY_FILTER_ID AND SFD.DATASET_ID IN (?1)
        INNER JOIN REPOSITORY.SECURITY_FILTER_ROLE SFR ON SFR.SECURITY_FILTER_ID = SF.SECURITY_FILTER_ID AND SFR.ROLE_ID IN (?2)
        """, nativeQuery = true)
    List<SecurityFilter> getAllByDataSetIdsAndRoleIds(Collection<Long> datasetIds, Collection<Long> roleIds);
}
