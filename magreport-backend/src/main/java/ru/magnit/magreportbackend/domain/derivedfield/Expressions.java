package ru.magnit.magreportbackend.domain.derivedfield;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.BaseExpression;
import ru.magnit.magreportbackend.expression.impl.AddExpression;
import ru.magnit.magreportbackend.expression.impl.ConstantValueExpression;
import ru.magnit.magreportbackend.expression.impl.DerivedFieldValueExpression;
import ru.magnit.magreportbackend.expression.impl.DivideExpression;
import ru.magnit.magreportbackend.expression.impl.LeftSubstrExpression;
import ru.magnit.magreportbackend.expression.impl.ModuloExpression;
import ru.magnit.magreportbackend.expression.impl.MultiplyExpression;
import ru.magnit.magreportbackend.expression.impl.ReportFieldValueExpression;
import ru.magnit.magreportbackend.expression.impl.RightSubstrExpression;
import ru.magnit.magreportbackend.expression.impl.StrLenExpression;
import ru.magnit.magreportbackend.expression.impl.SubstrExpression;
import ru.magnit.magreportbackend.expression.impl.SubtractExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Map;

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

    public BaseExpression init(FieldExpressionResponse fieldExpression, Map<FieldDefinition, Pair<Integer, DataTypeEnum>> fieldIndexes, String[][] resultCube){
        return switch (this){
            case CONSTANT_VALUE -> new ConstantValueExpression(fieldExpression, fieldIndexes, resultCube);
            case REPORT_FIELD_VALUE -> new ReportFieldValueExpression(fieldExpression, fieldIndexes, resultCube);
            case DERIVED_FIELD_VALUE -> new DerivedFieldValueExpression(fieldExpression, fieldIndexes, resultCube);
            case ADD -> new AddExpression(fieldExpression, fieldIndexes, resultCube);
            case SUBTRACT -> new SubtractExpression(fieldExpression, fieldIndexes, resultCube);
            case MULTIPLY -> new MultiplyExpression(fieldExpression, fieldIndexes, resultCube);
            case DIVIDE -> new DivideExpression(fieldExpression, fieldIndexes, resultCube);
            case MODULO -> new ModuloExpression(fieldExpression, fieldIndexes, resultCube);
            case STRLEN -> new StrLenExpression(fieldExpression, fieldIndexes, resultCube);
            case SUBSTR -> new SubstrExpression(fieldExpression, fieldIndexes, resultCube);
            case LEFT_SUBSTR -> new LeftSubstrExpression(fieldExpression, fieldIndexes, resultCube);
            case RIGHT_SUBSTR -> new RightSubstrExpression(fieldExpression, fieldIndexes, resultCube);
        };
    }
}
