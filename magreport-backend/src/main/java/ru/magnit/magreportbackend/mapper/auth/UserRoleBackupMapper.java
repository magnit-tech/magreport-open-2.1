package ru.magnit.magreportbackend.mapper.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.user.UserRole;
import ru.magnit.magreportbackend.dto.backup.user.UserRoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class UserRoleBackupMapper implements Mapper<UserRoleBackupTuple, UserRole> {
    @Override
    public UserRoleBackupTuple from(UserRole source) {
        return new UserRoleBackupTuple(
                source.getId(),
                source.getUser().getId(),
                source.getRole().getId(),
                source.getUserRoleType().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
