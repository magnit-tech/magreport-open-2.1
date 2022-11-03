package ru.magnit.magreportbackend.service.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.inner.UserView;
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldAddRequest;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;
import ru.magnit.magreportbackend.dto.response.derivedfield.ExpressionResponse;
import ru.magnit.magreportbackend.exception.InvalidParametersException;
import ru.magnit.magreportbackend.mapper.derivedfield.DerivedFieldMapper;
import ru.magnit.magreportbackend.mapper.derivedfield.DerivedFieldResponseMapper;
import ru.magnit.magreportbackend.mapper.derivedfield.ExpressionResponseMapper;
import ru.magnit.magreportbackend.repository.derivedfield.DerivedFieldExpressionRepository;
import ru.magnit.magreportbackend.repository.derivedfield.DerivedFieldRepository;
import ru.magnit.magreportbackend.repository.derivedfield.ExpressionRepository;
import ru.magnit.magreportbackend.util.Pair;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DerivedFieldDomainService {
    private final DerivedFieldRepository derivedFieldRepository;
    private final DerivedFieldExpressionRepository fieldExpressionRepository;
    private final ExpressionRepository expressionRepository;
    private final DerivedFieldMapper derivedFieldMapper;
    private final DerivedFieldResponseMapper derivedFieldResponseMapper;
    private final ExpressionResponseMapper expressionResponseMapper;

    @Transactional
    public void addDerivedField(DerivedFieldAddRequest request, UserView currentUser) {
        final var derivedField = derivedFieldMapper.from(new Pair<>(request, currentUser));

        derivedFieldRepository.save(derivedField);
    }

    @Transactional
    public DerivedFieldResponse getDerivedField(Long id) {
        final var derivedField = derivedFieldRepository.findById(id);
        if (derivedField.isEmpty()) {
            throw new InvalidParametersException("Производное поле с id: " + id + " не найдено.");
        }

        final var rootExpression = fieldExpressionRepository.getByDerivedFieldIdAndParentFieldExpressionIsNull(id);

        return derivedFieldResponseMapper.from(new Pair<>(derivedField.get(), rootExpression));
    }

    @Transactional
    public void deleteDerivedField(Long id) {
        derivedFieldRepository.deleteById(id);
    }

    @Transactional
    public void updateDerivedField(DerivedFieldAddRequest request, UserView currentUser) {
        fieldExpressionRepository.deleteAllByDerivedFieldId(request.getId());

        final var derivedField = derivedFieldMapper.from(new Pair<>(request, currentUser));
        derivedField.setId(request.getId());

        derivedFieldRepository.save(derivedField);
    }

    @Transactional
    public List<ExpressionResponse> getAllExpressions() {
        return expressionRepository.findAll()
            .stream()
            .map(expressionResponseMapper::from)
            .toList();
    }
}
