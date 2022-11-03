package ru.magnit.magreportbackend.dto.response.user;

import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Информация о доменной группе")
public class DomainGroupResponse {
    private Long domainId;
    private String domainName;
    private String groupName;
}
