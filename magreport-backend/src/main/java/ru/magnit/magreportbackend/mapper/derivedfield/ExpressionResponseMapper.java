package ru.magnit.magreportbackend.mapper.derivedfield;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.derivedfield.Expression;
import ru.magnit.magreportbackend.dto.response.derivedfield.ExpressionResponse;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ExpressionResponseMapper implements Mapper<ExpressionResponse, Expression> {
    private final NumParamTypeResponseMapper typeResponseMapper;

    @Override
    public ExpressionResponse from(Expression source) {
        return new ExpressionResponse()
            .setId(source.getId())
            .setName(source.getName())
            .setDescription(source.getDescription())
            .setNumParams(source.getNumParams())
            .setNumParamType(typeResponseMapper.from(source.getNumParamType()))
            .setUserId(source.getUser().getId())
            .setUserName(source.getUser().getName())
            .setCreated(source.getCreatedDateTime())
            .setModified(source.getModifiedDateTime());
    }
}
