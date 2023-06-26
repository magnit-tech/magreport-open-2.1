package ru.magnit.magreportbackend.domain.enums;

import lombok.RequiredArgsConstructor;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.BaseExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.agregate.*;
import ru.magnit.magreportbackend.expression.impl.*;

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
    ABS(AbsExpression::new),
    ZERO_TO_NULL(ZeroToNullExpression::new),
    IIF(IifExpression::new),
    SWITCH(SwitchExpression::new),
    LT(LtExpression::new),
    LTEQ(LteqExpression::new),
    EQ(EqExpression::new),
    LOGIC_AND(AndExpression::new),
    LOGIC_OR(OrExpression::new),
    LOGIC_XOR(XorExpression::new),
    LOGIC_NOT(NotExpression::new),
    ROUND(RoundExpression::new),
    FLOOR(FloorExpression::new),
    CEIL(CeilExpression::new),
    CONCAT(ConcatExpression::new),
    YEAR_FROM_DATE(YearFromDateExpression::new),
    MONTH_FROM_DATE(MonthFromDateExpression::new),
    WEEK_FROM_DATE(WeekFromDateExpression::new),
    DAY_FROM_DATE(DayFromDateExpression::new),
    DATE(DateExpression::new),
    DAYS_INTERVAL(DaysIntervalExpression::new),
    MILLSEC_INTERVAL(MillsecIntervalExtension::new),
    ADD_DAYS(AddDaysExpression::new),
    ADD_MILLSEC(AddMillsecExpression::new),
    ADD_WEEKS(AddWeeksExpression::new),
    ADD_MONTHS(AddMonthsExpression::new),
    MONTH_FIRST_DATE(MonthFirstDateExpression::new),
    MONTH_LAST_DATE(MonthLastDateExpression::new),
    TODAY(CurrentDateExpression::new),
    NEQ(NeqExpression::new),
    INTEGER_DIVISION(IntegerDivisionExpression::new),
    WEEK_DAY_NUMBER(WeekdayNumberExpression::new),
    NOW(NowExpression::new),
    TO_DATE(ToDateExpression::new),
    TO_DATETIME(ToDatetimeExpression::new),
    IS_NULL(IsNullExpression::new),
    GET_TYPE(GetTypeExpression::new),
    NULL_VALUE(NullValueExpression::new),
    COUNT(CountExpression::new),
    AVG(AvgExpression::new),
    MAX(MaxExpression::new),
    MIN(MinExpression::new),
    SUM(SumExpression::new),
    COUNT_DISTINCT(CountDistinctExpression::new),
    SPLIT(SplitExpression::new);

    private final BiFunction<FieldExpressionResponse, ExpressionCreationContext, BaseExpression> factory;

    public BaseExpression init(FieldExpressionResponse fieldExpression, ExpressionCreationContext context){
        return factory.apply(fieldExpression, context);
    }
}
