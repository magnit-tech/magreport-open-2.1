package ru.magnit.magreportbackend.domain.derivedfield;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.EntityWithName;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Entity(name = "NUM_PARAM_TYPE")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@AttributeOverride(name = "id", column = @Column(name = "NUM_PARAM_TYPE_ID"))
public class NumParamType extends EntityWithName {
    @Serial
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    public NumParamType(Long dataSourceTypeId) {
        this.id = dataSourceTypeId;
    }

    @SuppressWarnings("unused")
    public NumParamType(NumParamTypes paramTypes) {
        this.id = (long) paramTypes.ordinal();
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "numParamType")
    private List<Expression> expressions = Collections.emptyList();

    @Override
    public NumParamType setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public NumParamType setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public NumParamType setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public NumParamType setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
        return this;
    }

    @Override
    public NumParamType setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
        return this;
    }
}
