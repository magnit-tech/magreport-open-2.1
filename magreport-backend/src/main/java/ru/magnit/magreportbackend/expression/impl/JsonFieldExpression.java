package ru.magnit.magreportbackend.expression.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.exception.InvalidExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Objects;

public class JsonFieldExpression extends ParameterizedExpression {

    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.STRING);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonFieldExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {

        final var sourceJson = parameters.get(0).calculate(rowNumber);
        final var searchPath = parameters.get(1).calculate(rowNumber);

        if (Objects.isNull(sourceJson.getL())) return result;
        checkParameterHasAnyType(parameters.get(0), sourceJson, DataTypeEnum.STRING);

        checkParameterNotNull(parameters.get(1), searchPath);
        checkParameterHasAnyType(parameters.get(1), searchPath, DataTypeEnum.STRING);

        try {
            var path = "/" + searchPath.getL()
                    .replaceAll("[.\\[]", "/")
                    .replaceAll("]", "");

            var jsonTree = objectMapper.readTree(sourceJson.getL());
            var item = jsonTree.at(path);

            switch (item.getNodeType()) {
                case STRING, NUMBER, BINARY, BOOLEAN -> result.setL(item.asText(null));
                case NULL, MISSING -> result.setL(null);
                default -> result.setL(item.toString());
            }

        } catch (Exception ex) {
            throw new InvalidExpression(ex.getMessage());
        }

        return result;
    }


}






