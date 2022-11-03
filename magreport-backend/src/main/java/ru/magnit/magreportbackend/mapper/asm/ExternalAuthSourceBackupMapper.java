package ru.magnit.magreportbackend.mapper.asm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSource;
import ru.magnit.magreportbackend.dto.backup.external.ExternalAuthSourceBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ExternalAuthSourceBackupMapper implements Mapper<ExternalAuthSourceBackupTuple, ExternalAuthSource> {
    @Override
    public ExternalAuthSourceBackupTuple from(ExternalAuthSource source) {
        return new ExternalAuthSourceBackupTuple(
                source.getId(),
                source.getExternalAuth().getId(),
                source.getDataSet().getId(),
                source.getType().getId(),
                source.getPreSql(),
                source.getPostSql(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
