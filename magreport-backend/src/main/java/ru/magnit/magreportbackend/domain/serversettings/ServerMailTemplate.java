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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.io.Serial;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity(name = "SERVER_MAIL_TEMPLATE")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@AttributeOverride(name = "id", column = @Column(name = "SERVER_MAIL_TEMPLATE_ID"))
public class ServerMailTemplate extends EntityWithName {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "CODE")
    private String code;

    @Column(name = "SUBJECT")
    private String subject;

    @Lob
    @Column(name = "BODY")
    private String body;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "SERVER_MAIL_TEMPLATE_TYPE_ID")
    private ServerMailTemplateType type;

    @Override
    public ServerMailTemplate setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public ServerMailTemplate setCreatedDateTime(LocalDateTime createdDateTime) {
       this.createdDateTime = createdDateTime;
       return this;
    }

    @Override
    public ServerMailTemplate setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
        return this;
    }

    @Override
    public ServerMailTemplate setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public ServerMailTemplate setDescription(String description) {
        this.description = description;
        return this;
    }
}
