package ru.magnit.magreportbackend.domain.derivedfield;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.EntityWithName;
import ru.magnit.magreportbackend.domain.user.User;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Entity(name = "EXPRESSIONS")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@AttributeOverride(name = "id", column = @Column(name = "EXPRESSION_ID"))
public class Expression extends EntityWithName {
    @Serial
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    public Expression(Long id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public Expression(Expressions expression) {
        this.id = (long) expression.ordinal();
    }

    @Column(name = "NUM_PARAMS")
    private Long numParams;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NUM_PARAM_TYPE_ID")
    private NumParamType numParamType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "expression")
    private List<DerivedFieldExpression> derivedFieldExpressions = Collections.emptyList();

    @Override
    public Expression setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Expression setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Expression setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public Expression setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
        return this;
    }

    @Override
    public Expression setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
        return this;
    }
}
