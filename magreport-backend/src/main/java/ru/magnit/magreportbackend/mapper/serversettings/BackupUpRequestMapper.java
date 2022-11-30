package ru.magnit.magreportbackend.mapper.serversettings;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.backup.BackupUpRequest;
import ru.magnit.magreportbackend.exception.JsonSerializationException;
import ru.magnit.magreportbackend.mapper.Mapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupUpRequestMapper implements Mapper<BackupUpRequest, String> {
    @Override
    public BackupUpRequest from(String source) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(source, BackupUpRequest.class);
        } catch (Exception ex) {
            log.error("Error trying to deserialize BackupUpRequest.");
            throw new JsonSerializationException("Error trying to deserialize BackupUpRequest.", ex);
        }
    }
}
