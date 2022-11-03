package ru.magnit.magreportbackend.mapper.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.user.Domain;
import ru.magnit.magreportbackend.dto.response.user.DomainShortResponse;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DomainShortResponseMapper implements Mapper<DomainShortResponse, Domain> {
    @Override
    public DomainShortResponse from(Domain source) {
        return new DomainShortResponse(source.getId(),source.getName());
    }
}
