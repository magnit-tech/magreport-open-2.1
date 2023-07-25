package ru.magnit.magreportbackend.mapper.auth;

import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.user.User;
import ru.magnit.magreportbackend.dto.backup.user.UserTuple;
import ru.magnit.magreportbackend.mapper.Mapper;


@Service
public class UserTupleMapper implements Mapper<UserTuple, User> {
    @Override
    public UserTuple from(User source) {
        return new UserTuple(
                source.getId(),
                source.getDomain().getName(),
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
