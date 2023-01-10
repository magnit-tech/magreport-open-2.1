package ru.magnit.magreportbackend.dto.request.derivedfield;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DerivedFieldGetAvailableRequest {
    private Long reportId;
    private List<Long> additionalFields = Collections.emptyList();
}
