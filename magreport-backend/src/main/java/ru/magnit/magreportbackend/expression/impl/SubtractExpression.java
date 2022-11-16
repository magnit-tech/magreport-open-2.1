package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.exception.InvalidExpression;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Map;

public class SubtractExpression extends ParameterizedExpression {
    @SuppressWarnings("unused")
    public SubtractExpression(FieldExpressionResponse fieldExpression, Map<FieldDefinition, Pair<Integer, DataTypeEnum>> fieldIndexes, String[][] resultCube) {
        super(fieldExpression, fieldIndexes, resultCube);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        var result = 0D;
        var resultType = DataTypeEnum.INTEGER;
        var firstValue = true;
        for (var parameter: parameters) {
            final var parameterValue = parameter.calculate(rowNumber);

            if (parameterValue.getR().notIn(DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE)){
                throw new InvalidExpression("Функция SUBTRACT() не может принимать параметры типа " + parameterValue.getR());
            }
            resultType = resultType.widerNumeric(parameterValue.getR());

            if (firstValue) {
                result = Double.parseDouble(parameterValue.getL());
                firstValue = false;
                continue;
            }

            result -= Double.parseDouble(parameterValue.getL());
        }
        return new Pair<>(resultType.toTypedString(result), resultType);
    }
}
