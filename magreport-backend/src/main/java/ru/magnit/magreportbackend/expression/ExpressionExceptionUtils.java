package ru.magnit.magreportbackend.expression;

import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;

public interface ExpressionExceptionUtils {

    static String getParameterIsNullMessage(String expressionStack, DerivedFieldResponse derivedField, String functionName) {
        return "Ошибка при вычислении выражения производного поля \"" +
            derivedField.getName() + "\"(Id: " + derivedField.getId() + "):\n" +
            expressionStack +
            "\nФункция <" + functionName + "> не может принимать значения NULL;";
    }
}
