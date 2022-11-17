package ru.magnit.magreportbackend.expression;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Arrays;
import java.util.Map;

public record ExpressionCreationContext (
    Map<FieldDefinition, Pair<Integer, DataTypeEnum>> fieldIndexes,
    String[][] resultCube
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpressionCreationContext that = (ExpressionCreationContext) o;

        if (!fieldIndexes.equals(that.fieldIndexes)) return false;
        return Arrays.deepEquals(resultCube, that.resultCube);
    }

    @Override
    public int hashCode() {
        int result = fieldIndexes.hashCode();
        result = 31 * result + Arrays.deepHashCode(resultCube);
        return result;
    }

    @Override
    public String toString() {
        return "ExpressionCreationContext{" +
            "fieldIndexes=" + fieldIndexes +
            ", resultCube=" + Arrays.toString(resultCube) +
            '}';
    }
}
