package ru.magnit.magreportbackend.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.config.AuthConfig;
import ru.magnit.magreportbackend.config.data.LdapProperties;
import ru.magnit.magreportbackend.service.domain.DomainService;
import ru.magnit.magreportbackend.service.enums.LdapTypes;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MultiDomainAuthenticationManager implements AuthenticationManager {
    private final AuthConfig authConfig;
    private final DomainService domainService;
    private final Map<String, AuthenticationProvider> authProviders = new HashMap<>();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final var domainName = authentication.getDetails() == null ? authConfig.getDefaultDomain() : authentication.getDetails().toString();
        final var authProvider = authProviders.getOrDefault(domainName, authProviders.get(authConfig.getDefaultDomain()));
        return authProvider.authenticate(authentication);
    }

    @PostConstruct
    private void init() {
        domainService.addAllIfNotExists(authConfig.getDomains().keySet());
        authConfig.getDomains().forEach((key, value) -> {
            if (value.getType() == LdapTypes.AD) {
                authProviders.put(key, createADAuthProvider(value));
            } else if (value.getType() == LdapTypes.LDAP) {
                authProviders.put(key, createLDAPAuthProvider(value));
            }
        });
    }

    private AuthenticationProvider createADAuthProvider(LdapProperties ldapProperties) {
        final var adProvider = new ActiveDirectoryLdapAuthenticationProvider("", ldapProperties.getUrl(), ldapProperties.getBase());
        adProvider.setConvertSubErrorCodesToExceptions(true);
        adProvider.setUseAuthenticationRequestCredentials(true);
        return adProvider;
    }

    private AuthenticationProvider createLDAPAuthProvider(LdapProperties ldapProperties) {
        final var contextSource = new DefaultSpringSecurityContextSource(ldapProperties.getUrl());
        contextSource.setBase(ldapProperties.getBase());
        contextSource.setPooled(true);
        contextSource.afterPropertiesSet();

        final var authenticator = new BindAuthenticator(contextSource);
        authenticator.setUserDnPatterns(new String[]{ldapProperties.getUserFilter()});

        String groupSearchBase = "ou=groups";
        final var authoritiesPopulator = new DefaultLdapAuthoritiesPopulator(contextSource, groupSearchBase);
        authoritiesPopulator.setRolePrefix("");
        authoritiesPopulator.setGroupSearchFilter("uniqueMember={0}");

        return new LdapAuthenticationProvider(authenticator, authoritiesPopulator);
    }
}
