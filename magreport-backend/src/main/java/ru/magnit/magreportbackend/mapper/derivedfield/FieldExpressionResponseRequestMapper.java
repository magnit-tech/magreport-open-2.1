package ru.magnit.magreportbackend.mapper.derivedfield;

import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldExpressionAddRequest;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.mapper.Mapper;


@Service
public class FieldExpressionResponseRequestMapper implements Mapper<FieldExpressionResponse, DerivedFieldExpressionAddRequest> {
    @Override
    public FieldExpressionResponse from(DerivedFieldExpressionAddRequest source) {
        return new FieldExpressionResponse(
            source.getType(),
            source.getReferenceId(),
            source.getConstantValue(),
            source.getConstantType(),
            this.from(source.getParameters())
        );
    }
}
