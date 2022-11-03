package ru.magnit.magreportbackend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.magnit.magreportbackend.config.data.LdapProperties;
import ru.magnit.magreportbackend.util.Pair;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "magreport.auth-config")
public class AuthConfig {
    private String defaultDomain;
    private Map<String, LdapProperties> domains;

    public LdapProperties getDefaultDomainProperties() {
        return domains.get(defaultDomain);
    }

    public LdapProperties getDomainProperties(String domainName) {
        return domains.get(domainName);
    }

    public LdapProperties getDomainPropertiesOrDefault(String domainName) {
        return domains.getOrDefault(domainName, getDefaultDomainProperties());
    }

    @PostConstruct
    private void init() {
        domains = domains.entrySet().stream()
            .map(entry -> new Pair<>(entry.getKey().toUpperCase(), entry.getValue()))
            .distinct()
            .collect(Collectors.toMap(Pair::getL, Pair::getR));
    }
}
