package ru.magnit.magreportbackend.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.response.folder.FolderNodeResponse;
import ru.magnit.magreportbackend.dto.response.serversettings.ServerMailTemplateTypeResponse;
import ru.magnit.magreportbackend.mapper.serversettings.ServerMailTemplateTypeResponseMapper;
import ru.magnit.magreportbackend.repository.ServerMailTemplateTypeRepository;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerMailTemplateTypeDomainService {

    private final ServerMailTemplateTypeRepository repository;
    private final ServerMailTemplateTypeResponseMapper mapper;

    @Transactional
    public ServerMailTemplateTypeResponse getServerMailTemplateType(Long id) {

        if (id == null)
            return new ServerMailTemplateTypeResponse()
                    .setName("root")
                    .setChildTypes(mapper.from(repository.findAll()));
        else {
            var serverMailTemplateType = repository.getReferenceById(id);
            var response =  mapper.from(serverMailTemplateType);
            response.setPath(getPathById(id));
            return response;
        }
    }

    @Transactional
    public List<ServerMailTemplateTypeResponse> getAllServerMailTemplateType() {
        return mapper.from(repository.findAll());
    }

    public List<FolderNodeResponse> getPathById (Long id){
        var serverMailTemplateType = repository.getReferenceById(id);
        return Collections.singletonList(
                new FolderNodeResponse(
                        serverMailTemplateType.getId(),
                        null,
                        serverMailTemplateType.getName(),
                        serverMailTemplateType.getDescription(),
                        serverMailTemplateType.getCreatedDateTime(),
                        serverMailTemplateType.getModifiedDateTime()
                )
        );
    }

}
