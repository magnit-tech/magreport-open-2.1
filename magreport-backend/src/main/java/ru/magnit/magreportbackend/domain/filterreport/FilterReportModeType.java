package ru.magnit.magreportbackend.domain.filterreport;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.EntityWithName;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity(name = "FILTER_REPORT_MODE_TYPE")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@AttributeOverride(name = "id", column = @Column(name = "FILTER_REPORT_MODE_TYPE_ID"))
public class FilterReportModeType extends EntityWithName {


    @Override
    public FilterReportModeType setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
        return this;
    }

    @Override
    public FilterReportModeType setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
        return this;
    }

    @Override
    public FilterReportModeType setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public FilterReportModeType setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public FilterReportModeType setDescription(String description) {
        this.description = description;
        return this;
    }

    public FilterReportModeType(Long id) {
        this.id = id;
    }
}
