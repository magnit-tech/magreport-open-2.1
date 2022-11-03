package ru.magnit.magreportbackend.domain.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.EntityWithName;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity(name = "DOMAINS")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@AttributeOverride(name = "id", column = @Column(name = "DOMAIN_ID"))
public class Domain extends EntityWithName {
    @Serial
    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = ALL, mappedBy = "domain")
    private List<User> userRoles = Collections.emptyList();

    public Domain(Long domainId) {
        this.id = domainId;
    }

    @Override
    public Domain setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Domain setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Domain setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public Domain setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
        return this;
    }

    @Override
    public Domain setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
        return this;
    }
}
