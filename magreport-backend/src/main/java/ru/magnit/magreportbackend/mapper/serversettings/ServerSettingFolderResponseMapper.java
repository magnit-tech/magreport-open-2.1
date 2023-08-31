package ru.magnit.magreportbackend.mapper.serversettings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.serversettings.ServerSettingsFolder;
import ru.magnit.magreportbackend.dto.response.serversettings.ServerSettingsFolderResponse;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ServerSettingFolderResponseMapper implements Mapper<ServerSettingsFolderResponse, ServerSettingsFolder> {
    private final ServerSettingResponseMapper serverSettingResponseMapper;
    @Override
    public ServerSettingsFolderResponse from(ServerSettingsFolder source) {
        return new ServerSettingsFolderResponse()
                .setOrdinal(source.getOrdinal())
                .setCode(source.getCode())
                .setName(source.getName())
                .setDescription(source.getDescription())
                .setParameters(serverSettingResponseMapper.from(source.getSettings()));
    }
}
