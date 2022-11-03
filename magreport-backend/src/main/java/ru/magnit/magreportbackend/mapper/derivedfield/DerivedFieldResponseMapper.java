package ru.magnit.magreportbackend.mapper.derivedfield;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.derivedfield.DerivedField;
import ru.magnit.magreportbackend.domain.derivedfield.DerivedFieldExpression;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;
import ru.magnit.magreportbackend.mapper.Mapper;
import ru.magnit.magreportbackend.util.Pair;

@Service
@RequiredArgsConstructor
public class DerivedFieldResponseMapper implements Mapper<DerivedFieldResponse, Pair<DerivedField, DerivedFieldExpression>> {
    private final FieldExpressionResponseMapper fieldExpressionResponseMapper;

    @Override
    public DerivedFieldResponse from(Pair<DerivedField, DerivedFieldExpression> source) {

        return new DerivedFieldResponse()
            .setId(source.getL().getId())
            .setName(source.getL().getName())
            .setDescription(source.getL().getDescription())
            .setReportId(source.getL().getReport().getId())
            .setExpression(fieldExpressionResponseMapper.from(source.getR()))
            .setUserId(source.getL().getUser().getId())
            .setUserName(source.getL().getUser().getName())
            .setCreated(source.getL().getCreatedDateTime())
            .setModified(source.getL().getModifiedDateTime());
    }
}
