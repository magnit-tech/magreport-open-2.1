package ru.magnit.magreportbackend.expression;

import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;

public interface ExpressionExceptionUtils {
    String MESSAGE_HEADER = "Ошибка при вычислении выражения производного поля ";
    String FUNCTION_WORD = "\nФункция <";
    String ID_STUB = "\"(Id: ";

    static String getParameterIsNullMessage(String expressionStack, DerivedFieldResponse derivedField, String functionName) {
        return MESSAGE_HEADER +
            (derivedField == null ? "" :
                "\"" + derivedField.getName() + ID_STUB + derivedField.getId() + "):\n") +
            expressionStack +
            FUNCTION_WORD + functionName + "> не может принимать значения NULL;";
    }

    static String getWrongParameterTypeMessage(String expressionStack, DerivedFieldResponse derivedField, String functionName, String parameterType) {
        return MESSAGE_HEADER +
            (derivedField == null ? "" :
            "\"" + derivedField.getName() + ID_STUB + derivedField.getId() + "):\n") +
            expressionStack +
            FUNCTION_WORD + functionName + "> не может принимать значения типа '" + parameterType + "';";
    }

    static String getWrongParameterTypesMessage(String expressionStack, DerivedFieldResponse derivedField, String functionName, String parameterType1, String parameterType2) {
        return MESSAGE_HEADER +
            (derivedField == null ? "" :
                "\"" + derivedField.getName() + ID_STUB + derivedField.getId() + "):\n") +
            expressionStack +
            FUNCTION_WORD + functionName + "> не может принимать значения разных типов: '" + parameterType1 + "' и '" + parameterType2 + "';";
    }

    static String getNumberOfParametersMustBeOdd(String expressionStack, DerivedFieldResponse derivedField, String functionName) {
        return MESSAGE_HEADER +
            (derivedField == null ? "" :
                "\"" + derivedField.getName() + ID_STUB + derivedField.getId() + "):\n") +
            expressionStack +
            FUNCTION_WORD + functionName + "> не может принимать четное количество параметров;";
    }
}
