package ru.magnit.magreportbackend.dto.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;

public record DomainResponse (
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    Long id,

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    String name,

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    String description,

    @JsonFormat(shape = JsonFormat.Shape.BOOLEAN)
    boolean isDefault
){}
