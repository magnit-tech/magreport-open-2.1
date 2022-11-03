package ru.magnit.magreportbackend.mapper.asm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSourceField;
import ru.magnit.magreportbackend.dto.backup.external.ExternalAuthSourceFieldBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ExternalAuthSourceFieldBackupMapper implements Mapper<ExternalAuthSourceFieldBackupTuple, ExternalAuthSourceField> {
    @Override
    public ExternalAuthSourceFieldBackupTuple from(ExternalAuthSourceField source) {
        return new ExternalAuthSourceFieldBackupTuple(
                source.getId(),
                source.getSource().getTypeEnum().getId(),
                source.getSource().getId(),
                source.getDataSetField().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
