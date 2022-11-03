package ru.magnit.magreportbackend.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Component;
import ru.magnit.magreportbackend.config.AuthConfig;
import ru.magnit.magreportbackend.config.data.LdapProperties;

import javax.annotation.PostConstruct;
import javax.naming.directory.SearchControls;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LdapTemplateManager {
    private final AuthConfig authConfig;
    private final Map<String, LdapTemplate> ldapTemplates = new HashMap<>();

    public LdapTemplate getLdapTemplate(String domainName) {
        return ldapTemplates.get(domainName);
    }

    @PostConstruct
    private void init() {
        authConfig.getDomains().forEach((key, value) -> ldapTemplates.put(key, createLdapTemplate(value)));
    }

    private LdapTemplate createLdapTemplate(LdapProperties ldapProperties) {
        final var contextSource = new LdapContextSource();
        contextSource.setUrl(ldapProperties.getUrl());
        contextSource.setBase(ldapProperties.getBase());
        contextSource.setUserDn(ldapProperties.getUserDn());
        contextSource.setPassword(ldapProperties.getPassword());
        contextSource.setAnonymousReadOnly(ldapProperties.getUserDn().isEmpty() && ldapProperties.getPassword().isEmpty());
        contextSource.setPooled(true);
        contextSource.afterPropertiesSet();

        final var ldapTemplate = new LdapTemplate(contextSource);
        ldapTemplate.setDefaultSearchScope(SearchControls.SUBTREE_SCOPE);
        ldapTemplate.setIgnorePartialResultException(true);
        return ldapTemplate;
    }
}
