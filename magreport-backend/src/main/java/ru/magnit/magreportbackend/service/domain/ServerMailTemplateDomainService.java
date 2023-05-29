package ru.magnit.magreportbackend.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.serversettings.ServerMailTemplate;
import ru.magnit.magreportbackend.domain.serversettings.ServerMailTemplateType;
import ru.magnit.magreportbackend.domain.user.User;
import ru.magnit.magreportbackend.dto.request.folder.FolderSearchRequest;
import ru.magnit.magreportbackend.dto.request.serversettings.ServerMailTemplateEditRequest;
import ru.magnit.magreportbackend.dto.response.folder.FolderSearchResponse;
import ru.magnit.magreportbackend.dto.response.folder.FolderSearchResultResponse;
import ru.magnit.magreportbackend.dto.response.serversettings.ServerMailTemplateResponse;
import ru.magnit.magreportbackend.dto.response.serversettings.ServerMailTemplateSearchResultResponse;
import ru.magnit.magreportbackend.mapper.serversettings.FolderNodeResponseServerMailTemplateTypeMapper;
import ru.magnit.magreportbackend.mapper.serversettings.ServerMailTemplateResponseMapper;
import ru.magnit.magreportbackend.repository.ServerMailTemplateRepository;
import ru.magnit.magreportbackend.repository.ServerMailTemplateTypeRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerMailTemplateDomainService {

    private final ServerMailTemplateRepository repository;
    private final ServerMailTemplateTypeRepository serverMailTemplateTypeRepository;
    private final ServerMailTemplateResponseMapper mapper;

    private final FolderNodeResponseServerMailTemplateTypeMapper folderNodeResponseServerMailTemplateTypeMapper;
    @Transactional
    public ServerMailTemplateResponse getServerMailTemplate (Long id) {
        return mapper.from(repository.getReferenceById(id));
    }
    @Transactional
    public void editServerMailTemplate (ServerMailTemplateEditRequest request,Long userId) {

        var template = repository.getReferenceById(request.getId());
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setSubject(request.getSubject());
        template.setBody(request.getBody());
        template.setModifiedDateTime(LocalDateTime.now());
        template.setUser(new User().setId(userId));


        repository.save(template);
    }
    @Transactional
    public List<ServerMailTemplateResponse> getServerMailTemplateByType (Long id) {
        var type = serverMailTemplateTypeRepository.getReferenceById(id);
        return mapper.from(type.getServerMailTemplates());
    }
    @Transactional
    public List<ServerMailTemplateResponse> getAllServerMailTemplate () {
        return mapper.from(repository.findAll());
    }

    @Transactional
    public FolderSearchResponse searchTemplate(FolderSearchRequest request) {
        var result = new FolderSearchResponse(new LinkedList<>(), new LinkedList<>());

        if (request.getRootFolderId() == null) {
            final var templateTypes = serverMailTemplateTypeRepository.findAll();
            templateTypes.forEach(templateType -> searchTemplate(templateType, request, result));
            return result;
        } else {
            final var templateType = serverMailTemplateTypeRepository.getReferenceById(request.getRootFolderId());
            return searchTemplate(templateType, request, result);
        }
    }

    private FolderSearchResponse searchTemplate(ServerMailTemplateType templateType, FolderSearchRequest request, FolderSearchResponse result) {
        if (templateType == null) return result;

        final var roles = templateType.getServerMailTemplates()
                .stream()
                .filter(role -> request.getLikenessType().check(request.getSearchString(), role.getName()))
                .toList();

        roles.forEach(role -> result.objects().add(mapRoles(templateType, role)));

        if (request.getLikenessType().check(request.getSearchString(), templateType.getName()))
            result.folders().add(mapFolder(templateType));

        return result;
    }

    private ServerMailTemplateSearchResultResponse mapRoles(ServerMailTemplateType path, ServerMailTemplate template) {
        return new ServerMailTemplateSearchResultResponse(Collections.singletonList(folderNodeResponseServerMailTemplateTypeMapper.from(path)), mapper.from(template));

    }

    private FolderSearchResultResponse mapFolder(ServerMailTemplateType path) {
        return new FolderSearchResultResponse(
                Collections.singletonList(folderNodeResponseServerMailTemplateTypeMapper.from(path)), folderNodeResponseServerMailTemplateTypeMapper.from(path));
    }

}

