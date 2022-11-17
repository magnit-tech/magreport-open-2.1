package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.exception.InvalidExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class AddExpression extends ParameterizedExpression {
    public AddExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
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
            final var paramResult = parameter.calculate(rowNumber).getL();
            if (paramResult != null) {
                result += Double.parseDouble(paramResult);
            }
        }
        return new Pair<>(resultType.toTypedString(result), resultType);
    }
}
