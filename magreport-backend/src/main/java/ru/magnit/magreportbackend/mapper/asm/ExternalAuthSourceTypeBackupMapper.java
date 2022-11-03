package ru.magnit.magreportbackend.mapper.asm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSourceType;
import ru.magnit.magreportbackend.dto.backup.external.ExternalAuthSourceTypeBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ExternalAuthSourceTypeBackupMapper implements Mapper<ExternalAuthSourceTypeBackupTuple, ExternalAuthSourceType> {
    @Override
    public ExternalAuthSourceTypeBackupTuple from(ExternalAuthSourceType source) {
        return new ExternalAuthSourceTypeBackupTuple(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
