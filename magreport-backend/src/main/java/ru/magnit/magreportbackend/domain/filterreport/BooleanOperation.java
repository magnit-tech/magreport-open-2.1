package ru.magnit.magreportbackend.domain.filterreport;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.EntityWithName;
import ru.magnit.magreportbackend.domain.enums.BinaryBooleanOperations;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Entity(name = "BOOLEAN_OPERATION")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@AttributeOverride(name = "id", column = @Column(name = "BOOLEAN_OPERATION_ID"))
public class BooleanOperation extends EntityWithName {

    @Serial
    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "type")
    private List<FilterReportGroup> groups = Collections.emptyList();

    public BooleanOperation(Long id) {
        this.id = id;
    }

    public BooleanOperation(BinaryBooleanOperations operType) {
        this.id = (long) operType.ordinal();
    }

    @Override
    public BooleanOperation setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public BooleanOperation setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public BooleanOperation setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public BooleanOperation setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
        return this;
    }

    @Override
    public BooleanOperation setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
        return this;
    }
}
