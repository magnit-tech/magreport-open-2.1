package ru.magnit.magreportbackend.domain.derivedfield;

import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.exception.InvalidParametersException;
import ru.magnit.magreportbackend.expression.BaseExpression;
import ru.magnit.magreportbackend.expression.impl.AddExpression;
import ru.magnit.magreportbackend.expression.impl.ConstantValueExpression;
import ru.magnit.magreportbackend.expression.impl.DivideExpression;
import ru.magnit.magreportbackend.expression.impl.LeftSubstrExpression;
import ru.magnit.magreportbackend.expression.impl.ModuloExpression;
import ru.magnit.magreportbackend.expression.impl.MultiplyExpression;
import ru.magnit.magreportbackend.expression.impl.RightSubstrExpression;
import ru.magnit.magreportbackend.expression.impl.StrLenExpression;
import ru.magnit.magreportbackend.expression.impl.SubstrExpression;
import ru.magnit.magreportbackend.expression.impl.SubtractExpression;

public enum Expressions {
    CONSTANT_VALUE,
    REPORT_FIELD_VALUE,
    DERIVED_FIELD_VALUE,
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    MODULO,
    STRLEN,
    SUBSTR,
    LEFT_SUBSTR,
    RIGHT_SUBSTR;

    public BaseExpression init(FieldExpressionResponse fieldExpression){
        return switch (this){
            case CONSTANT_VALUE -> new ConstantValueExpression(fieldExpression);
            case REPORT_FIELD_VALUE -> throw new InvalidParametersException("Wrong type: " + this);
            case DERIVED_FIELD_VALUE -> throw new InvalidParametersException("Wrong type: " + this);
            case ADD -> new AddExpression(fieldExpression);
            case SUBTRACT -> new SubtractExpression(fieldExpression);
            case MULTIPLY -> new MultiplyExpression(fieldExpression);
            case DIVIDE -> new DivideExpression(fieldExpression);
            case MODULO -> new ModuloExpression(fieldExpression);
            case STRLEN -> new StrLenExpression(fieldExpression);
            case SUBSTR -> new SubstrExpression(fieldExpression);
            case LEFT_SUBSTR -> new LeftSubstrExpression(fieldExpression);
            case RIGHT_SUBSTR -> new RightSubstrExpression(fieldExpression);
        };
    }
}
