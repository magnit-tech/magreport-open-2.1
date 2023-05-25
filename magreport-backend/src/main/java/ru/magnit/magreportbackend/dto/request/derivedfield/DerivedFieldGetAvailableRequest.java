package ru.magnit.magreportbackend.dto.request.derivedfield;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.Set;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DerivedFieldGetAvailableRequest {
    private Long reportId;
    private Set<Long> additionalFields = Collections.emptySet();
}
