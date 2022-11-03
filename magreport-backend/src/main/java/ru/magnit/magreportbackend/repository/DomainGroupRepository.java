package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.magnit.magreportbackend.domain.user.DomainGroup;

@Repository
public interface DomainGroupRepository extends JpaRepository<DomainGroup, Long> {
    DomainGroup getByDomainIdAndName(Long domainId, String groupName);
    boolean existsByDomainIdAndName(Long domainId, String groupName);
}
