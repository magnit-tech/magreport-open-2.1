package ru.magnit.magreportbackend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldAddRequest;
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldRequest;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;
import ru.magnit.magreportbackend.dto.response.derivedfield.ExpressionResponse;
import ru.magnit.magreportbackend.service.domain.DerivedFieldDomainService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DerivedFieldService {
    private final DerivedFieldDomainService domainService;
    private final UserDomainService userDomainService;

    public DerivedFieldResponse getDerivedField(DerivedFieldRequest request) {
        return domainService.getDerivedField(request.getId());
    }

    public void addDerivedField(DerivedFieldAddRequest request) {
        domainService.addDerivedField(request, userDomainService.getCurrentUser());
    }

    public void deleteDerivedField(DerivedFieldRequest request) {
        domainService.deleteDerivedField(request.getId());
    }

    public void updateDerivedField(DerivedFieldAddRequest request) {
        domainService.updateDerivedField(request, userDomainService.getCurrentUser());
    }

    public List<ExpressionResponse> getAllExpressions() {
        return domainService.getAllExpressions();
    }
}
