package ru.magnit.magreportbackend.expression;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.util.Pair;

public interface BaseExpression {

    Pair<String, DataTypeEnum> calculate(int rowNumber);
}
