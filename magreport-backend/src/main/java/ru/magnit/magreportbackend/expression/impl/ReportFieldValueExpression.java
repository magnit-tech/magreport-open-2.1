package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldTypes;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.BaseExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Map;

public class ReportFieldValueExpression implements BaseExpression {
    private final int columnIndex;
    private final String[][] resultCube;
    private final Pair<String, DataTypeEnum> result;

    public ReportFieldValueExpression(FieldExpressionResponse fieldExpression, Map<FieldDefinition, Pair<Integer, DataTypeEnum>> fieldIndexes, String[][] resultCube) {
        final var fieldDefinition = new FieldDefinition(fieldExpression.getReferenceId(), OlapFieldTypes.REPORT_FIELD);
        this.resultCube = resultCube;
        this.columnIndex = fieldIndexes.get(fieldDefinition).getL();
        /// !!! Для сокращения генерации мусора, не запускать параллельно
        this.result = new Pair<>(null, fieldIndexes.get(fieldDefinition).getR());
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        result.setL(resultCube[columnIndex][rowNumber]);
        return result;
    }
}
