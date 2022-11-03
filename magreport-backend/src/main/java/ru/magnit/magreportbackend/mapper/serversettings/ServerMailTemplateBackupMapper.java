package ru.magnit.magreportbackend.mapper.serversettings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.serversettings.ServerMailTemplate;
import ru.magnit.magreportbackend.dto.backup.serversettings.ServerMailTemplateBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ServerMailTemplateBackupMapper implements Mapper<ServerMailTemplateBackupTuple, ServerMailTemplate> {
    @Override
    public ServerMailTemplateBackupTuple from(ServerMailTemplate source) {
        return new ServerMailTemplateBackupTuple(
                source.getId(),
                source.getType().getId(),
                source.getUser().getId(),
                source.getCode(),
                source.getSubject(),
                source.getBody(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
