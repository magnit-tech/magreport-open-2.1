package ru.magnit.magreportbackend.domain.serversettings;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.EntityWithName;
import ru.magnit.magreportbackend.domain.user.User;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Entity(name = "SERVER_MAIL_TEMPLATE_TYPE")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@AttributeOverride(name = "id", column = @Column(name = "SERVER_MAIL_TEMPLATE_TYPE_ID"))
public class ServerMailTemplateType extends EntityWithName {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToMany(cascade = ALL, mappedBy = "type")
    private List<ServerMailTemplate> serverMailTemplates = Collections.emptyList();

    @Override
    public ServerMailTemplateType setId(Long id) {
         this.id = id;
         return this;
    }

    @Override
    public ServerMailTemplateType setCreatedDateTime(LocalDateTime createdDateTime) {
      this.createdDateTime = createdDateTime;
      return this;
    }

    @Override
    public ServerMailTemplateType setModifiedDateTime(LocalDateTime modifiedDateTime) {
      this.modifiedDateTime = modifiedDateTime;
      return this;
    }

    @Override
    public ServerMailTemplateType setName(String name) {
       this.name = name;
       return this;
    }

    @Override
    public ServerMailTemplateType setDescription(String description) {
       this.description = description;
       return this;
    }
}
