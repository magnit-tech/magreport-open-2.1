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

@Entity(name = "TAGS")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@AttributeOverride(name = "id", column = @Column(name = "TAG_ID"))
public class Tag extends EntityWithName {
    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private Tag parentTag;

    @Column(name = "CODE")
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag")
    private List<ExpressionTag> tags = Collections.emptyList();

    @SuppressWarnings("unused")
    public Tag(Long id) { this.id = id; }

    @Override
    public Tag setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Tag setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Tag setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public Tag setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
        return this;
    }

    @Override
    public Tag setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
        return this;
    }
}
