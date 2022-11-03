package ru.magnit.magreportbackend.dto.response.derivedfield;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ExpressionResponse {
    private Long id;
    private String name;
    private String description;
    private Long numParams;
    private NumParamTypeResponse numParamType;
    private Long userId;
    private String userName;
    private LocalDateTime created;
    private LocalDateTime modified;
}
