package ru.magnit.magreportbackend.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.config.AuthConfig;
import ru.magnit.magreportbackend.domain.user.Domain;
import ru.magnit.magreportbackend.repository.DomainRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DomainService {
    private final DomainRepository domainRepository;
    private final AuthConfig authConfig;

    @Transactional
    public void addAllIfNotExists(Collection<String> domainNames) {
        final var newDomains = domainNames
            .stream()
            .filter(name -> !domainRepository.existsByName(name))
            .map(name -> new Domain().setName(name).setDescription(authConfig.getDomainProperties(name).getDescription()))
            .toList();

        domainRepository.saveAll(newDomains);
    }

    @Transactional
    public Map<String, Long> getIdMap(Collection<String> domainNames) {
        return domainRepository.findAllByNameIn(domainNames)
            .stream()
            .collect(Collectors.toMap(Domain::getName, Domain::getId));
    }
}
