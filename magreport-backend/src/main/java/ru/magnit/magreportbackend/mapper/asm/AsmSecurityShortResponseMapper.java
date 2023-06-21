package ru.magnit.magreportbackend.mapper.asm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.asm.ExternalAuth;
import ru.magnit.magreportbackend.dto.response.asm.AsmSecurityShortResponse;
import ru.magnit.magreportbackend.mapper.Mapper;
import ru.magnit.magreportbackend.mapper.auth.RoleTypeResponseMapper;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AsmSecurityShortResponseMapper implements Mapper<AsmSecurityShortResponse, ExternalAuth> {

    private final RoleTypeResponseMapper roleTypeResponseMapper;

    @Override
    public AsmSecurityShortResponse from(ExternalAuth source) {
        return new AsmSecurityShortResponse(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getUser().getName(),
                roleTypeResponseMapper.shallowMap(source.getRoleType()),
                source.getIsDefaultDomain(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime(),
                Collections.emptyList(),
                source.getIsActive());
    }
}
