package ru.magnit.magreportbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.config.AuthConfig;
import ru.magnit.magreportbackend.dto.request.folder.FolderPathRequest;
import ru.magnit.magreportbackend.dto.response.folder.FolderPathResponse;
import ru.magnit.magreportbackend.dto.response.user.DomainResponse;
import ru.magnit.magreportbackend.service.domain.DataSetDomainService;
import ru.magnit.magreportbackend.service.domain.DataSourceDomainService;
import ru.magnit.magreportbackend.service.domain.DomainService;
import ru.magnit.magreportbackend.service.domain.ExcelTemplateDomainService;
import ru.magnit.magreportbackend.service.domain.FilterInstanceDomainService;
import ru.magnit.magreportbackend.service.domain.FilterTemplateDomainService;
import ru.magnit.magreportbackend.service.domain.FolderDomainService;
import ru.magnit.magreportbackend.service.domain.ReportDomainService;
import ru.magnit.magreportbackend.service.domain.SecurityFilterDomainService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuxiliaryService {
    private final AuthConfig authConfig;
    private final DomainService domainService;
    private final ReportDomainService reportDomainService;
    private final FolderDomainService folderDomainService;
    private final DataSourceDomainService dataSourceDomainService;
    private final DataSetDomainService dataSetDomainService;
    private final ExcelTemplateDomainService excelTemplateDomainService;
    private final FilterInstanceDomainService filterInstanceDomainService;
    private final FilterTemplateDomainService filterTemplateDomainService;
    private final SecurityFilterDomainService securityFilterDomainService;

    public FolderPathResponse getFolderPath(FolderPathRequest request) {
        return new FolderPathResponse(switch (request.getFolderType()) {
            case PUBLISHED_REPORT -> folderDomainService.getPathToFolder(request.getId());
            case REPORT_FOLDER -> reportDomainService.getPathToFolder(request.getId());
            case DATASOURCE -> dataSourceDomainService.getPathToFolder(request.getId());
            case DATASET -> dataSetDomainService.getPathToFolder(request.getId());
            case EXCEL_TEMPLATE -> excelTemplateDomainService.getPathToFolder(request.getId());
            case FILTER_INSTANCE -> filterInstanceDomainService.getPathToFolder(request.getId());
            case FILTER_TEMPLATE -> filterTemplateDomainService.getPathToFolder(request.getId());
            case SECURITY_FILTER -> securityFilterDomainService.getPathToFolder(request.getId());
        });
    }

    public List<DomainResponse> getDomains(){
        final var idMap = domainService.getIdMap(authConfig.getDomains().keySet());
        return authConfig.getDomains().entrySet()
            .stream()
            .map(entry -> new DomainResponse(
                idMap.get(entry.getKey()),
                entry.getKey(),
                entry.getValue().getDescription(),
                authConfig.getDefaultDomain().equals(entry.getKey())
            ))
            .toList();
    }
}
