package ru.magnit.magreportbackend.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "Запрос информации о пользователе")
public class UserRequest {

    private String userName;
    private String domain;
}
