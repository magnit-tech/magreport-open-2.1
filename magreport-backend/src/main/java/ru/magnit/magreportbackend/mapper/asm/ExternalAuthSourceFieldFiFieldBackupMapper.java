package ru.magnit.magreportbackend.mapper.asm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSourceFieldFilterInstanceField;
import ru.magnit.magreportbackend.dto.backup.external.ExternalAuthSourceFieldFIFieldBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ExternalAuthSourceFieldFiFieldBackupMapper implements Mapper<ExternalAuthSourceFieldFIFieldBackupTuple, ExternalAuthSourceFieldFilterInstanceField> {

    @Override
    public ExternalAuthSourceFieldFIFieldBackupTuple from(ExternalAuthSourceFieldFilterInstanceField source) {
        return new ExternalAuthSourceFieldFIFieldBackupTuple(
                source.getId(),
                source.getSourceField().getId(),
                source.getFilterInstanceField().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
