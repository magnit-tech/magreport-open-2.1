package ru.magnit.magreportbackend.dto.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;

public record DomainShortResponse (

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    Long id,

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    String name
){}
