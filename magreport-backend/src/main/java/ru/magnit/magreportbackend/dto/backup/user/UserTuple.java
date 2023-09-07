package ru.magnit.magreportbackend.dto.backup.user;

import java.time.LocalDateTime;

public record UserTuple(
        Long userId,
        String domainName,
        String name,
        String firstName,
        String patronymic,
        String lastName,
        String email,
        String description,
        LocalDateTime created,
        LocalDateTime modified
){}
