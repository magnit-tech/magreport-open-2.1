package ru.magnit.magreportbackend.service.domain.asm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.inner.asm.ExternalAuthSourceView;

@Slf4j
@Service
public class ExternalAuthUserRoleRefreshQueryBuilder {

    private static final String LINE_FEED =  "\n\t";

    public String buildQuery(ExternalAuthSourceView securitySource, boolean isDefaultDomain) {

        String result = isDefaultDomain ? getQueryWithOutDomain(securitySource) :  getQueryWithDomain(securitySource);
        log.debug("Ams UserRoles refresh query:\n" + result);
        return result;
    }

    private String getQueryWithOutDomain(ExternalAuthSourceView securitySource) {
        return "SELECT\n\t" +
                securitySource.getChangeTypeField().getDataSetField().getName() +
                "," + LINE_FEED +
                securitySource.getUserNameField().getDataSetField().getName() +
                "," + LINE_FEED +
                securitySource.getRoleNameField().getDataSetField().getName() +
                "\nFROM " +  LINE_FEED +
                securitySource.getDataSet().getSchemaName() + "." +
                securitySource.getDataSet().getObjectName();

    }

    private String getQueryWithDomain(ExternalAuthSourceView securitySource) {
        return "SELECT\n\t" +
                securitySource.getChangeTypeField().getDataSetField().getName() +
                "," +  LINE_FEED +
                securitySource.getUserNameField().getDataSetField().getName() +
                "," + LINE_FEED +
                securitySource.getRoleNameField().getDataSetField().getName() +
                "," + LINE_FEED +
                securitySource.getDomainNameField().getDataSetField().getName() +
                "\nFROM " + LINE_FEED +
                securitySource.getDataSet().getSchemaName() + "." +
                securitySource.getDataSet().getObjectName();

    }
}
