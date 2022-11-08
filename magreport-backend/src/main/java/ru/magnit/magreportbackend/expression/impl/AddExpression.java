package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.exception.InvalidExpression;
import ru.magnit.magreportbackend.expression.BaseExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class AddExpression implements BaseExpression {
    private final List<BaseExpression> parameters = new ArrayList<>();

    public AddExpression(FieldExpressionResponse fieldExpression) {
        for (var parameter: fieldExpression.getParameters()) {
            parameters.add(parameter.getType().init(parameter));
        }
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        var result = 0D;
        var resultType = DataTypeEnum.INTEGER;
        for (var parameter: parameters) {
            final var parameterValue = parameter.calculate(rowNumber);
            if (parameterValue.getR().notIn(DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE)){
                throw new InvalidExpression("Функция ADD() не может принимать параметры типа " + parameterValue.getR());
            }

            resultType = resultType.widerNumeric(parameterValue.getR());
            result += Double.parseDouble(parameter.calculate(rowNumber).getL());
        }
        return new Pair<>(resultType.toTypedString(result), resultType);
    }
}
