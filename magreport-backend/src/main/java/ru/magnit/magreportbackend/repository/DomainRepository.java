package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.magnit.magreportbackend.domain.user.Domain;

import java.util.Collection;
import java.util.List;

@Repository
public interface DomainRepository extends JpaRepository<Domain, Long> {
    Domain getByName(String domainName);
    boolean existsByName(String domainName);
    List<Domain> findAllByNameIn(Collection<String> names);
}
