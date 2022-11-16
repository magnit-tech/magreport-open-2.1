package ru.magnit.magreportbackend.dto.request.olap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FieldDefinition {
    private Long fieldId;
    private OlapFieldTypes fieldType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldDefinition fieldDefinition = (FieldDefinition) o;

        if (!getFieldId().equals(fieldDefinition.getFieldId())) return false;
        return getFieldType() == fieldDefinition.getFieldType();
    }

    @Override
    public int hashCode() {
        int result = getFieldId().hashCode();
        result = 31 * result + getFieldType().hashCode();
        return result;
    }
}
