package ru.magnit.magreportbackend.mapper.dataset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.request.dataset.DataSetFieldAddRequest;
import ru.magnit.magreportbackend.dto.response.datasource.ObjectFieldResponse;
import ru.magnit.magreportbackend.mapper.Mapper;

import java.sql.JDBCType;

@Service
@RequiredArgsConstructor
public class DataSetFieldAddRequestMapper implements Mapper<DataSetFieldAddRequest, ObjectFieldResponse> {

    @Override
    public DataSetFieldAddRequest from(ObjectFieldResponse source) {
        return mapBaseProperties(source);
    }

    private DataSetFieldAddRequest mapBaseProperties(ObjectFieldResponse source) {

        var typeId = (long) DataTypeEnum.valueOf(JDBCType.valueOf(source.getDataType())).ordinal();

        return new DataSetFieldAddRequest()
                .setTypeId(typeId)
                .setName(source.getFieldName())
                .setDescription(source.getRemarks())
                .setIsValid(typeId != DataTypeEnum.UNKNOWN.ordinal());
    }
}
