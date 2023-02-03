package ru.magnit.magreportbackend.dto.request.derivedfield;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
public class DerivedFieldCheckNameRequest {
    private Long reportId;
    private Boolean isPublic = false;
    private String name;
}
