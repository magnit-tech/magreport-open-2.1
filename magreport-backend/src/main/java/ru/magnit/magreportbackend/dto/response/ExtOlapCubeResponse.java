package ru.magnit.magreportbackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.magnit.magreportbackend.dto.response.olap.OlapCubeResponse;

@Getter
@ToString
@NoArgsConstructor
public class ExtOlapCubeResponse {
    @Schema(description = "Флаг успешности операции")
    private Boolean success;

    @Schema(description = "Текстовое сообщение о статусе операции, описание ошибки")
    private String message;

    @Schema(description = "Данные ответа")
    private OlapCubeResponse data;
}
