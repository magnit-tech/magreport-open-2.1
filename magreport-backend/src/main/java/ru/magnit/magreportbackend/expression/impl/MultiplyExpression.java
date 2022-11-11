package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.exception.InvalidExpression;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class MultiplyExpression extends ParameterizedExpression {
    public MultiplyExpression(FieldExpressionResponse fieldExpression) {
        super(fieldExpression);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        var result = 1D;
        var resultType = DataTypeEnum.INTEGER;
        for (var parameter: parameters) {
            final var parameterValue = parameter.calculate(rowNumber);
            if (parameterValue.getR().notIn(DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE)){
                throw new InvalidExpression("Функция MULTIPLY() не может принимать параметры типа " + parameterValue.getR());
            }
            resultType = resultType.widerNumeric(parameterValue.getR());

            result *= Double.parseDouble(parameter.calculate(rowNumber).getL());
        }
        return new Pair<>(resultType.toTypedString(result), resultType);
    }
}
