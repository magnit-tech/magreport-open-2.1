package ru.magnit.magreportbackend.mapper.asm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSourceFieldType;
import ru.magnit.magreportbackend.dto.backup.external.ExternalAuthSourceFieldTypeBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ExternalAuthSourceFieldTypeBackupMapper implements Mapper<ExternalAuthSourceFieldTypeBackupTuple, ExternalAuthSourceFieldType> {
    @Override
    public ExternalAuthSourceFieldTypeBackupTuple from(ExternalAuthSourceFieldType source) {
        return new ExternalAuthSourceFieldTypeBackupTuple(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
