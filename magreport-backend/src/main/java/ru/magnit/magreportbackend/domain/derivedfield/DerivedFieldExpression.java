package ru.magnit.magreportbackend.domain.derivedfield;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.BaseEntity;
import ru.magnit.magreportbackend.domain.dataset.DataType;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.domain.report.ReportField;
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

@Entity(name = "DERIVED_FIELD_EXPRESSION")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@AttributeOverride(name = "id", column = @Column(name = "DERIVED_FIELD_EXPRESSION_ID"))
public class DerivedFieldExpression extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    public DerivedFieldExpression(Long id) { this.id = id; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DERIVED_FIELD_ID")
    private DerivedField derivedField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_FIELD_EXPRESSION_ID")
    private DerivedFieldExpression parentFieldExpression;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXPRESSION_ID")
    private Expression expression;

    @Column(name = "ORDINAL")
    private Integer ordinal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARAMETER_DERIVED_FIELD_ID")
    private DerivedField parameterDerivedField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARAMETER_REPORT_FIELD_ID")
    private ReportField parameterReportField;

    @Lob
    @Column(name = "PARAMETER_CONSTANT_VALUE")
    private String parameterConstantValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONSTANT_DATA_TYPE_ID")
    private DataType parameterConstantDataType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentFieldExpression")
    private List<DerivedFieldExpression> parameterExpressions = Collections.emptyList();

    public Expressions getExpressionType(){
        return Expressions.values()[this.expression.getId().intValue()];
    }

    public Long getReferenceId(){
        if (parameterReportField != null) return parameterReportField.getId();
        if (parameterDerivedField != null) return parameterDerivedField.getId();
        return null;
    }

    public DataTypeEnum getConstantDataType() {
        if (parameterConstantDataType == null) return null;
        return parameterConstantDataType.getEnum();
    }

    @Override
    public DerivedFieldExpression setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public DerivedFieldExpression setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
        return this;
    }

    @Override
    public DerivedFieldExpression setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
        return this;
    }
}
