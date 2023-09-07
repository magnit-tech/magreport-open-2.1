package ru.magnit.magreportbackend.mapper.serversettings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.serversettings.ServerSettings;
import ru.magnit.magreportbackend.dto.response.serversettings.ServerParameterResponse;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ServerSettingResponseMapper implements Mapper<ServerParameterResponse, ServerSettings> {
    @Override
    public ServerParameterResponse from(ServerSettings source) {
        return new ServerParameterResponse()
                .setId(source.getId())
                .setCode(source.getCode())
                .setEncoded(source.isEncoded())
                .setName(source.getName())
                .setDescription(source.getDescription())
                .setValue(source.isEncoded() ? "*****" : source.getValue());
    }
}
