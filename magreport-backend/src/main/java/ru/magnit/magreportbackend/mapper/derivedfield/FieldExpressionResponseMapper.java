package ru.magnit.magreportbackend.mapper.derivedfield;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.derivedfield.DerivedFieldExpression;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FieldExpressionResponseMapper implements Mapper<FieldExpressionResponse, DerivedFieldExpression> {

    @Override
    public FieldExpressionResponse from(DerivedFieldExpression source) {
        return new FieldExpressionResponse()
            .setType(source.getExpressionType())
            .setReferenceId(source.getReferenceId())
            .setConstantValue(source.getParameterConstantValue())
            .setConstantType(source.getConstantDataType())
            .setParameters(source
                .getParameterExpressions()
                .stream()
                .map(this::from)
                .toList());
    }
}
