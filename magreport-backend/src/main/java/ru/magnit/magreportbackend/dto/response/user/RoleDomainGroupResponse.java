package ru.magnit.magreportbackend.dto.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

public record RoleDomainGroupResponse(
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    RoleResponse role,

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    List<DomainGroupResponse> domainGroups
) {
    public boolean isEmpty() {
        return domainGroups.isEmpty();
    }
}
