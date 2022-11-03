package ru.magnit.magreportbackend.mapper.serversettings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.serversettings.ServerSettings;
import ru.magnit.magreportbackend.dto.backup.serversettings.ServerSettingsBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ServerSettingsBackupMapper implements Mapper<ServerSettingsBackupTuple, ServerSettings> {
    @Override
    public ServerSettingsBackupTuple from(ServerSettings source) {
        return new ServerSettingsBackupTuple(
                source.getId(),
                source.getOrdinal(),
                source.getFolder().getId(),
                source.isEncoded(),
                source.getCode(),
                source.getName(),
                source.getDescription(),
                source.getValue(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
