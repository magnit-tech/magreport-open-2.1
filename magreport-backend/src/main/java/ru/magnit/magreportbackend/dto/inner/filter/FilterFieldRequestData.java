package ru.magnit.magreportbackend.dto.inner.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;

@Getter
@AllArgsConstructor
@Accessors(chain = true)
public class FilterFieldRequestData {

   Long fieldId;
   String fieldName;
   DataTypeEnum fieldType;
   Boolean  showField;
   Boolean searchByField;

}
