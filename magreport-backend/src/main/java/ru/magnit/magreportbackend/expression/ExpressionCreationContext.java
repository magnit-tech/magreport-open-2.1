package ru.magnit.magreportbackend.expression;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Arrays;
import java.util.Map;

public record ExpressionCreationContext (
    Map<FieldDefinition, Pair<Integer, DataTypeEnum>> fieldIndexes,
    String[][] resultCube,
    DerivedFieldResponse derivedField
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpressionCreationContext that = (ExpressionCreationContext) o;

        return derivedField.equals(that.derivedField);
    }

    @Override
    public int hashCode() {
        return derivedField.hashCode();
    }

    @Override
    public String toString() {
        return "ExpressionCreationContext{" +
            "fieldIndexes=" + fieldIndexes +
            ", resultCube=" + Arrays.toString(resultCube) +
            ", derivedField=" + derivedField +
            '}';
    }
}
