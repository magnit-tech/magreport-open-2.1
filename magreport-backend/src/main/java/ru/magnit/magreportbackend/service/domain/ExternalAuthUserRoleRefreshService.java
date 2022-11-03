package ru.magnit.magreportbackend.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.config.AuthConfig;
import ru.magnit.magreportbackend.domain.user.UserRoleTypeEnum;
import ru.magnit.magreportbackend.dto.inner.asm.ExternalAuthSecurityView;
import ru.magnit.magreportbackend.service.UserService;
import ru.magnit.magreportbackend.service.domain.asm.ExternalAuthUserRoleRefreshQueryBuilder;
import ru.magnit.magreportbackend.service.jobengine.filter.FilterQueryExecutor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ru.magnit.magreportbackend.domain.asm.ExternalAuthSourceTypeEnum.USER_MAP_SOURCE;


@Service
@RequiredArgsConstructor
public class ExternalAuthUserRoleRefreshService {

    private static final String ROLE_NAME = "role_name";
    private static final String USER_NAME = "user_name";
    private static final String CHANGE_TYPE = "change_type";
    private static final String DOMAIN_NAME = "domain_name";
    private final ExternalAuthUserRoleRefreshQueryBuilder queryBuilder;
    private final FilterQueryExecutor filterQuery;
    private final UserService userService;
    private final AuthConfig authConfig;

    public void refreshUserRoles(ExternalAuthSecurityView securityView) {
        var roleNamesSource = securityView.getSources().get(USER_MAP_SOURCE);

        filterQuery.executeSql(roleNamesSource.getDataSet().getDataSource(), roleNamesSource.getPreSql());

        List<Map<String, String>> queryResult = filterQuery.getQueryResult(roleNamesSource.getDataSet().getDataSource(), queryBuilder.buildQuery(roleNamesSource, securityView.isDefaultDomain()));

        processRecords(queryResult, securityView.isDefaultDomain());

        filterQuery.executeSql(roleNamesSource.getDataSet().getDataSource(), roleNamesSource.getPostSql());
    }

    private void processRecords(List<Map<String, String>> records, boolean isDefaultValue) {


        records
                .forEach(currentRecord -> {
                    userService.loginUser(isDefaultValue ? authConfig.getDefaultDomain() : currentRecord.get(DOMAIN_NAME).toUpperCase(), currentRecord.get(USER_NAME).toLowerCase(), Collections.emptyList());
                    userService.clearRoles(currentRecord.get(USER_NAME).toLowerCase(), isDefaultValue ? authConfig.getDefaultDomain() : currentRecord.get(DOMAIN_NAME).toUpperCase(), Collections.singletonList(currentRecord.get(ROLE_NAME)));
                });

        records.stream()
                .filter(currentRecord -> currentRecord.get(CHANGE_TYPE).equals("I"))
                .forEach(currentRecord ->
                        userService.setUserRoles(
                                currentRecord.get(USER_NAME).toLowerCase(),
                                isDefaultValue ? authConfig.getDefaultDomain() : currentRecord.get(DOMAIN_NAME).toUpperCase(),
                                Collections.singletonList(currentRecord.get(ROLE_NAME)),
                                UserRoleTypeEnum.ASM)
                );
    }
}
