package ru.magnit.magreportbackend.domain.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import ru.magnit.magreportbackend.domain.user.User;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

import static org.hibernate.id.enhanced.SequenceStyleGenerator.CONFIG_PREFER_SEQUENCE_PER_ENTITY;
import static org.hibernate.id.enhanced.SequenceStyleGenerator.CONFIG_SEQUENCE_PER_ENTITY_SUFFIX;

@Entity(name = "EVENT")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "EVENT_ID"))
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @GenericGenerator(
            name = "sequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = CONFIG_PREFER_SEQUENCE_PER_ENTITY, value = "true"),
                    @Parameter(name = CONFIG_SEQUENCE_PER_ENTITY_SUFFIX, value = "_SEQ")
            })
    @EqualsAndHashCode.Include
    @Column(name = "ID")
    @Schema(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "IP_ADDRESS")
    private String ipAddress;

    @Column(name = "EVENT_TYPE")
    private String eventType;

    @Lob
    @Column(name = "ADDITIONALLY")
    private String additionally;

    @CreationTimestamp
    @Column(name = "CREATED", nullable = false, updatable = false)
    protected LocalDateTime createdDateTime;

}
