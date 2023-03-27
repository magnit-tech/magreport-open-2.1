package ru.magnit.magreportbackend.domain.enums;

import lombok.RequiredArgsConstructor;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.BaseExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.impl.AbsExpression;
import ru.magnit.magreportbackend.expression.impl.AddExpression;
import ru.magnit.magreportbackend.expression.impl.ConstantValueExpression;
import ru.magnit.magreportbackend.expression.impl.CurrentDateExpression;
import ru.magnit.magreportbackend.expression.impl.DerivedFieldValueExpression;
import ru.magnit.magreportbackend.expression.impl.DivideExpression;
import ru.magnit.magreportbackend.expression.impl.IifExpression;
import ru.magnit.magreportbackend.expression.impl.LeftSubstrExpression;
import ru.magnit.magreportbackend.expression.impl.ModuloExpression;
import ru.magnit.magreportbackend.expression.impl.MultiplyExpression;
import ru.magnit.magreportbackend.expression.impl.NvlExpression;
import ru.magnit.magreportbackend.expression.impl.PowExpression;
import ru.magnit.magreportbackend.expression.impl.ReplaceExpression;
import ru.magnit.magreportbackend.expression.impl.ReportFieldValueExpression;
import ru.magnit.magreportbackend.expression.impl.RightSubstrExpression;
import ru.magnit.magreportbackend.expression.impl.RowNumberExpression;
import ru.magnit.magreportbackend.expression.impl.SquareRootExpression;
import ru.magnit.magreportbackend.expression.impl.StrLenExpression;
import ru.magnit.magreportbackend.expression.impl.SubstrExpression;
import ru.magnit.magreportbackend.expression.impl.SubtractExpression;
import ru.magnit.magreportbackend.expression.impl.ToDoubleExpression;
import ru.magnit.magreportbackend.expression.impl.ToIntegerExpression;
import ru.magnit.magreportbackend.expression.impl.ToStringExpression;
import ru.magnit.magreportbackend.expression.impl.ZeroToNullExpression;

import java.util.function.BiFunction;

@RequiredArgsConstructor
public enum Expressions {
    CONSTANT_VALUE(ConstantValueExpression::new),
    REPORT_FIELD_VALUE(ReportFieldValueExpression::new),
    DERIVED_FIELD_VALUE(DerivedFieldValueExpression::new),
    ADD(AddExpression::new),
    SUBTRACT(SubtractExpression::new),
    MULTIPLY(MultiplyExpression::new),
    DIVIDE(DivideExpression::new),
    MODULO(ModuloExpression::new),
    STRLEN(StrLenExpression::new),
    SUBSTR(SubstrExpression::new),
    LEFT_SUBSTR(LeftSubstrExpression::new),
    RIGHT_SUBSTR(RightSubstrExpression::new),
    NVL(NvlExpression::new),
    REPLACE(ReplaceExpression::new),
    TO_STRING(ToStringExpression::new),
    TO_INTEGER(ToIntegerExpression::new),
    TO_DOUBLE(ToDoubleExpression::new),
    CURRENT_DATE(CurrentDateExpression::new),
    ROW_NUMBER(RowNumberExpression::new),
    SQUARE_ROOT(SquareRootExpression::new),
    POW(PowExpression::new),
    ABS_EXPRESSION(AbsExpression::new),
    ZERO_TO_NULL_EXPRESSION(ZeroToNullExpression::new),
    IIF_EXPRESSION(IifExpression::new);

    private final BiFunction<FieldExpressionResponse, ExpressionCreationContext, BaseExpression> factory;

    public BaseExpression init(FieldExpressionResponse fieldExpression, ExpressionCreationContext context){
        return factory.apply(fieldExpression, context);
    }
}
