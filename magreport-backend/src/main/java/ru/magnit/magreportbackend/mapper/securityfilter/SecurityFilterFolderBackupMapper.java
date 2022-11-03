package ru.magnit.magreportbackend.mapper.securityfilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterFolder;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterFolderBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class SecurityFilterFolderBackupMapper implements Mapper<SecurityFilterFolderBackupTuple, SecurityFilterFolder> {
    @Override
    public SecurityFilterFolderBackupTuple from(SecurityFilterFolder source) {
        return new SecurityFilterFolderBackupTuple(
                source.getId(),
                source.getParentFolder() == null ? null : source.getParentFolder().getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
