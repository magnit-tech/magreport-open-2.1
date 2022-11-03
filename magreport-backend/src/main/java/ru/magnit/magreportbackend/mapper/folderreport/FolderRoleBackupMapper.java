package ru.magnit.magreportbackend.mapper.folderreport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.folderreport.FolderRole;
import ru.magnit.magreportbackend.dto.backup.folder.FolderRoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FolderRoleBackupMapper implements Mapper<FolderRoleBackupTuple, FolderRole> {
    @Override
    public FolderRoleBackupTuple from(FolderRole source) {
        return new FolderRoleBackupTuple(
                source.getId(),
                source.getFolder().getId(),
                source.getRole().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
