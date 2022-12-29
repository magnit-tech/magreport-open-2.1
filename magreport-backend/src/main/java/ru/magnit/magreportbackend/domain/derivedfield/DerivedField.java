package ru.magnit.magreportbackend.domain.derivedfield;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.EntityWithName;
import ru.magnit.magreportbackend.domain.report.Report;
import ru.magnit.magreportbackend.domain.user.User;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity(name = "DERIVED_FIELD")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@AttributeOverride(name = "id", column = @Column(name = "DERIVED_FIELD_ID"))
public class DerivedField extends EntityWithName {
    @Serial
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    public DerivedField(Long id) { this.id = id; }

    @Lob
    @Column(name = "EXPRESSION_TEXT")
    private String expressionText;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "derivedField")
    private List<DerivedFieldExpression> derivedFieldExpressions = Collections.emptyList();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID")
    private Report report;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Override
    public DerivedField setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public DerivedField setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public DerivedField setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public DerivedField setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
        return this;
    }

    @Override
    public DerivedField setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
        return this;
    }
}
