package ru.magnit.magreportbackend.mapper.derivedfield;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataType;
import ru.magnit.magreportbackend.domain.derivedfield.DerivedField;
import ru.magnit.magreportbackend.domain.derivedfield.DerivedFieldExpression;
import ru.magnit.magreportbackend.domain.derivedfield.Expression;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.domain.report.Report;
import ru.magnit.magreportbackend.domain.report.ReportField;
import ru.magnit.magreportbackend.domain.user.User;
import ru.magnit.magreportbackend.dto.inner.UserView;
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldAddRequest;
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldExpressionAddRequest;
import ru.magnit.magreportbackend.mapper.Mapper;
import ru.magnit.magreportbackend.util.Pair;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DerivedFieldMapper implements Mapper<DerivedField, Pair<DerivedFieldAddRequest, UserView>> {

    @Override
    public DerivedField from(Pair<DerivedFieldAddRequest, UserView> source) {
        final var derivedField = new DerivedField()
            .setIsPublic(source.getL().getIsPublic())
            .setReport(new Report(source.getL().getReportId()))
            .setExpressionText(source.getL().getExpressionText())
            .setDataType(new DataType(1L))
            .setUser(new User(source.getR().getId()))
            .setName(source.getL().getName())
            .setUniqueName(source.getL().getUniqueName(source.getR().getId()))
            .setDescription(source.getL().getDescription());

        if (source.getL().getExpression() != null) {
            derivedField.setDerivedFieldExpressions(unwindExpression(
                source.getL().getExpression(),
                derivedField,
                null,
                1,
                source.getR().getId()
                ));
        }

        return derivedField;
    }

    List<DerivedFieldExpression> unwindExpression(DerivedFieldExpressionAddRequest request, DerivedField derivedField, DerivedFieldExpression parentFieldExpression, int ordinal, Long userId) {
        final var result = new ArrayList<DerivedFieldExpression>();

        final var fieldExpression = new DerivedFieldExpression()
            .setDerivedField(derivedField)
            .setParentFieldExpression(parentFieldExpression)
            .setExpression(new Expression(request.getType()))
            .setOrdinal(ordinal)
            .setParameterDerivedField(request.getType() == Expressions.DERIVED_FIELD_VALUE ? new DerivedField(request.getReferenceId()) : null)
            .setParameterReportField(request.getType() == Expressions.REPORT_FIELD_VALUE ? new ReportField(request.getReferenceId()) : null)
            .setParameterConstantValue(request.getType() == Expressions.CONSTANT_VALUE ? request.getConstantValue() : null)
            .setParameterConstantDataType(request.getType() == Expressions.CONSTANT_VALUE ? new DataType(request.getConstantType()) : null)
            .setUser(new User(userId));

        result.add(fieldExpression);

        var index = 1;
        for (final var parameterExpression: request.getParameters()) {
            result.addAll(unwindExpression(
                parameterExpression,
                derivedField,
                fieldExpression,
                index++,
                userId
            ));
        }

        return result;
    }
}
