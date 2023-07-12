package ru.magnit.magreportbackend.service.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.config.AuthConfig;
import ru.magnit.magreportbackend.dto.response.user.UserResponse;
import ru.magnit.magreportbackend.service.enums.LdapTypes;
import ru.magnit.magreportbackend.service.security.LdapTemplateManager;
import ru.magnit.magreportbackend.util.Pair;
import ru.magnit.magreportbackend.util.Triple;

import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LdapService {

    private final LdapTemplateManager ldapTemplateManager;
    private final AuthConfig authConfig;

    public List<String> getGroupsByNamePart(String domainName, String namePart) {
        final var ldapTemplate = ldapTemplateManager.getLdapTemplate(domainName);
        final var ldapProperties = authConfig.getDomainProperties(domainName);

        return Arrays.stream(ldapProperties.getGroupPaths())
                .map(path ->
                        ldapTemplate.search(
                                path,
                                "cn=*" + namePart + "*",
                                SearchControls.SUBTREE_SCOPE,
                                (AttributesMapper<String>) attributes -> String.valueOf(attributes.get("cn").get())))
                .flatMap(Collection::stream)
                .toList();
    }

    public String getUserFullName(String domainName, String loginName) {
        final var ldapTemplate = ldapTemplateManager.getLdapTemplate(domainName);
        final var ldapProperties = authConfig.getDomainProperties(domainName);
        final var displayNames = new ArrayList<String>();

        try {
            Arrays.stream(ldapProperties.getUserPaths())
                    .map(path ->
                            ldapTemplate.search(
                                    path,
                                    ldapProperties.getUserSearchFilter().replace("{0}", loginName),
                                    SearchControls.SUBTREE_SCOPE,
                                    (AttributesMapper<String>) attributes -> String.valueOf(attributes.get(ldapProperties.getFullNameParamName()).get())
                            )
                    )
                    .flatMap(Collection::stream)
                    .filter(Predicate.not(String::isBlank))
                    .forEach(displayNames::add);
        } catch (Exception ignored) {
        }

        return displayNames.isEmpty() ? "" : displayNames.get(0);
    }

    public String getUserEmail(String domainName, String loginName) {
        final var ldapTemplate = ldapTemplateManager.getLdapTemplate(domainName);
        final var ldapProperties = authConfig.getDomainProperties(domainName);
        final var mails = new ArrayList<String>();

        try {
            Arrays.stream(ldapProperties.getUserPaths())
                    .map(path ->
                            ldapTemplate.search(
                                    path,
                                    ldapProperties.getUserSearchFilter().replace("{0}", loginName),
                                    SearchControls.SUBTREE_SCOPE,
                                    (AttributesMapper<String>) attributes -> {
                                        try {
                                            return String.valueOf(attributes.get(ldapProperties.getMailParamName()).get());
                                        } catch (Exception ex) {
                                            return "";
                                        }
                                    }))
                    .flatMap(Collection::stream)
                    .filter(Predicate.not(String::isBlank))
                    .forEach(mails::add);
        } catch (Exception ignored) {
        }

        return mails.isEmpty() ? "" : mails.get(0);
    }

    public Map<String, Pair<String, String>> getUserInfo(List<UserResponse> users) {
        var batch = new HashMap<String, ArrayList<String>>();
        var results = new HashMap<String, Pair<String, String>>();
        users.forEach(user -> {
            final var ldapProperties = authConfig.getDomainProperties(user.getDomain().name());
            if (Objects.isNull(ldapProperties)) return;

            batch.putIfAbsent(user.getDomain().name(), new ArrayList<>());
            if (batch.get(user.getDomain().name()).size() < ldapProperties.getBatchSize())
                batch.get(user.getDomain().name()).add(user.getName());
            else {
                results.putAll(searchInLdap(user.getDomain().name(), batch.get(user.getDomain().name())));
                batch.get(user.getDomain().name()).clear();
            }
        });

        batch.forEach((key, value) -> results.putAll(searchInLdap(key, value)));

        return results;
    }

    private Map<String, Pair<String, String>> searchInLdap(String domainName, List<String> logins) {
        final var ldapTemplate = ldapTemplateManager.getLdapTemplate(domainName);
        final var ldapProperties = authConfig.getDomainProperties(domainName);

        if (ldapProperties.getType() == LdapTypes.LDAP) return Collections.emptyMap();

        var filter = "(|" + logins.stream()
                .map(login -> String.format("(%s)", ldapProperties.getUserSearchFilter().replace("{0}", login)))
                .collect(Collectors.joining()) + ")";

        return Arrays.stream(ldapProperties.getUserPaths())

                .map(path -> ldapTemplate.search(
                            path,
                            filter,
                            SearchControls.SUBTREE_SCOPE,
                            (AttributesMapper<Triple<String, String, String>>) attributes -> {
                                var result = new Triple<String, String, String>();
                                result.setA(domainName + "\\" + attributes.get(ldapProperties.getLoginParamName()).get().toString().toLowerCase());
                                try {
                                    result.setB(String.valueOf(attributes.get(ldapProperties.getMailParamName()).get()));
                                } catch (Exception ex) {
                                    result.setB("");
                                }
                                try {
                                    result.setC(String.valueOf(attributes.get(ldapProperties.getFullNameParamName()).get()));
                                } catch (Exception ex) {
                                    result.setC("");
                                }
                                return result;
                            }))
                .flatMap(Collection::stream)
                .collect(
                        Collectors.toMap(
                                Triple::getA,
                                t -> new Pair<String, String>().setL(t.getB()).setR(t.getC()),
                                (pair1, pair2) -> pair1
                 ));
    }
}
