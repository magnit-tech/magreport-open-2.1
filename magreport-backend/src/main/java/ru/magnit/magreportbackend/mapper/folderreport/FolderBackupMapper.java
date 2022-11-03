package ru.magnit.magreportbackend.mapper.folderreport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.folderreport.Folder;
import ru.magnit.magreportbackend.dto.backup.folder.FolderBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FolderBackupMapper implements Mapper<FolderBackupTuple, Folder> {
    @Override
    public FolderBackupTuple from(Folder source) {
        return new FolderBackupTuple(
                source.getId(),
                source.getParentFolder() == null ? null : source.getParentFolder().getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
