package ru.magnit.magreportbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.request.folder.FolderSearchRequest;
import ru.magnit.magreportbackend.dto.request.serversettings.ServerMailTemplateEditRequest;
import ru.magnit.magreportbackend.dto.request.serversettings.ServerMailTemplateRequest;
import ru.magnit.magreportbackend.dto.request.serversettings.ServerMailTemplateTypeRequest;
import ru.magnit.magreportbackend.dto.response.folder.FolderSearchResponse;
import ru.magnit.magreportbackend.dto.response.serversettings.ServerMailTemplateResponse;
import ru.magnit.magreportbackend.dto.response.serversettings.ServerMailTemplateTypeResponse;
import ru.magnit.magreportbackend.service.domain.ServerMailTemplateDomainService;
import ru.magnit.magreportbackend.service.domain.ServerMailTemplateTypeDomainService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerMailTemplateService {

    private final ServerMailTemplateDomainService serverMailTemplateDomainService;
    private final ServerMailTemplateTypeDomainService serverMailTemplateTypeDomainService;

    private final UserDomainService userDomainService;

    public ServerMailTemplateTypeResponse getServerMailTemplateType(ServerMailTemplateTypeRequest request) {
        return serverMailTemplateTypeDomainService.getServerMailTemplateType(request.getId());
    }

    public List<ServerMailTemplateTypeResponse> getAllServerMailTemplateType() {
        return serverMailTemplateTypeDomainService.getAllServerMailTemplateType();
    }

    public void editServerMailTemplate(ServerMailTemplateEditRequest request) {
        var currentUser = userDomainService.getCurrentUser();
        serverMailTemplateDomainService.editServerMailTemplate(request, currentUser.getId());
    }

    @Transactional
    public ServerMailTemplateResponse getServerMailTemplate(ServerMailTemplateRequest request) {
        var response  =  serverMailTemplateDomainService.getServerMailTemplate(request.getId());
        response.setPath(serverMailTemplateTypeDomainService.getPathById(response.getType().getId()));
        return response;
    }

    public List<ServerMailTemplateResponse> getServerMailTemplateByType(ServerMailTemplateTypeRequest request) {
        return serverMailTemplateDomainService.getServerMailTemplateByType(request.getId());
    }

    public List<ServerMailTemplateResponse> getAllServerMailTemplate() {
        return serverMailTemplateDomainService.getAllServerMailTemplate();
    }

    public FolderSearchResponse searchTemplate(FolderSearchRequest request) {
        return serverMailTemplateDomainService.searchTemplate(request);
    }
}
