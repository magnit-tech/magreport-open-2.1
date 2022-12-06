package ru.magnit.magreportbackend.mapper.securityfilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterFolder;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterFolderBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class SecurityFilterFolderRestoreMapper implements Mapper<SecurityFilterFolder, SecurityFilterFolderBackupTuple> {
    @Override
    public SecurityFilterFolder from(SecurityFilterFolderBackupTuple source) {
        return new SecurityFilterFolder()
                .setId(source.securityFilterFolderId())
                .setName(source.name())
                .setDescription(source.description())
                .setParentFolder(new SecurityFilterFolder(source.parentId()));
    }
}
