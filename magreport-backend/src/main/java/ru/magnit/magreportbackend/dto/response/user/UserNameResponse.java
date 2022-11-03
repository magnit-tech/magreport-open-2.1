package ru.magnit.magreportbackend.dto.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;

public record UserNameResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        String name,

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        String domain
) {
}
