package ru.magnit.magreportbackend.domain.filterreport;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.BaseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity(name = "FILTER_REPORT_MODE")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@AttributeOverride(name = "id", column = @Column(name = "FILTER_REPORT_MODE_ID"))
public class FilterReportMode extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILTER_REPORT_MODE_TYPE_ID")
    private FilterReportModeType filterReportModeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILTER_REPORT_ID")
    private FilterReport filterReport;


    @Override
    public FilterReportMode setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public FilterReportMode setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
        return this;
    }

    @Override
    public FilterReportMode setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
        return this;
    }

    public FilterReportMode(FilterReportModeType filterReportModeType) {
        this.filterReportModeType = filterReportModeType;
    }
}
