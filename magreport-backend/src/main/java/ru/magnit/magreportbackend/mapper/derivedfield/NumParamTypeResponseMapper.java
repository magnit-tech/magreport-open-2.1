package ru.magnit.magreportbackend.mapper.derivedfield;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.derivedfield.NumParamType;
import ru.magnit.magreportbackend.dto.response.derivedfield.NumParamTypeResponse;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class NumParamTypeResponseMapper implements Mapper<NumParamTypeResponse, NumParamType> {
    @Override
    public NumParamTypeResponse from(NumParamType source) {
        return new NumParamTypeResponse()
            .setId(source.getId())
            .setName(source.getName())
            .setDescription(source.getDescription())
            .setCreated(source.getCreatedDateTime())
            .setModified(source.getModifiedDateTime());
    }
}
