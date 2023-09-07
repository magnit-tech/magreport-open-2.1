package ru.magnit.magreportbackend.mapper.serversettings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.serversettings.ServerMailTemplateType;
import ru.magnit.magreportbackend.dto.response.folder.FolderNodeResponse;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FolderNodeResponseServerMailTemplateTypeMapper implements Mapper<FolderNodeResponse, ServerMailTemplateType>  {
    @Override
    public FolderNodeResponse from(ServerMailTemplateType source) {
        return new FolderNodeResponse(
                source.getId(),
                null,
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
