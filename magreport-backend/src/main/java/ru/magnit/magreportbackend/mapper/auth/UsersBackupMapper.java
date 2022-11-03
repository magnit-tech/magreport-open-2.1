package ru.magnit.magreportbackend.mapper.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.user.User;
import ru.magnit.magreportbackend.dto.backup.user.UsersBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class UsersBackupMapper implements Mapper<UsersBackupTuple, User> {
    @Override
    public UsersBackupTuple from(User source) {
        return new UsersBackupTuple(
                source.getId(),
                source.getUserStatus().getId(),
                source.getName(),
                source.getFirstName(),
                source.getPatronymic(),
                source.getLastName(),
                source.getEmail(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
