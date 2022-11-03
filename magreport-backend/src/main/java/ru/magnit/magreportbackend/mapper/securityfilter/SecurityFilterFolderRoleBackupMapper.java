package ru.magnit.magreportbackend.mapper.securityfilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterFolderRole;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterFolderRoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class SecurityFilterFolderRoleBackupMapper implements Mapper<SecurityFilterFolderRoleBackupTuple, SecurityFilterFolderRole> {
    @Override
    public SecurityFilterFolderRoleBackupTuple from(SecurityFilterFolderRole source) {
        return new SecurityFilterFolderRoleBackupTuple(
                source.getId(),
                source.getFolder().getId(),
                source.getRole().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
