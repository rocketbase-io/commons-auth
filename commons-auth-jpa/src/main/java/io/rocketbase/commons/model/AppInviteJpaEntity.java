package io.rocketbase.commons.model;

import io.rocketbase.commons.model.converter.StringListConverter;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Entity
@Table(name = "co_invite")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AppInviteJpaEntity implements AppInviteEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    private String invitor;

    @Column(length = 4000)
    private String message;

    private String firstName;

    private String lastName;

    private String email;

    @Column(name = "roles")
    @Convert(converter = StringListConverter.class)
    private List<String> roles;

    @ElementCollection
    @CollectionTable(
            name = "co_invite_keyvalue",
            joinColumns = @JoinColumn(name = "invite_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_invite_keyvalue", columnNames = {"invite_id", "field_key"}),
            indexes = @Index(name = "idx_invite_keyvalue", columnList = "invite_id")
    )
    @MapKeyColumn(name = "field_key", length = 50)
    @Lob
    @Column(name = "field_value", nullable = false)
    @Builder.Default
    private Map<String, String> keyValueMap = new HashMap<>();

    @CreatedDate
    @Column(nullable = false)
    private Instant created;

    @Column(nullable = false)
    private Instant expiration;

    @Override
    public Map<String, String> getKeyValues() {
        return keyValueMap;
    }

    @Override
    public void setKeyValues(Map<String, String> map) {
        this.keyValueMap = map;
    }

    public AppInviteJpaEntity(String id) {
        this.id = id;
    }
}
