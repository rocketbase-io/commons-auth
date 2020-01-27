package io.rocketbase.commons.model;

import io.rocketbase.commons.model.converter.StringListConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Entity
@Table(name = "invite", uniqueConstraints = {
        @UniqueConstraint(name = "uk_invite_email", columnNames = {"email"})})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppInviteJpaEntity implements AppInviteEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @NotNull
    private String invitor;

    @Column(length = 4000)
    private String message;

    private String firstName;

    private String lastName;

    @NotNull
    @Email
    private String email;

    @Column(name = "roles")
    @Convert(converter = StringListConverter.class)
    private List<String> roles;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "invite_keyvalue_pairs",
            joinColumns = @JoinColumn(name = "invite_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_invite_keyvalue_pairs", columnNames = {"invite_id", "field_key"}),
            indexes = @Index(name = "idx_invite_keyvalue_pairs", columnList = "invite_id")
    )
    @MapKeyColumn(name = "field_key", length = 50)
    @Column(name = "field_value", length = 4000, nullable = false)
    @Builder.Default
    private Map<String, String> keyValueMap = new HashMap<>();

    @NotNull
    @CreatedDate
    @Column(nullable = false)
    private Instant created;

    @NotNull
    @Column(nullable = false)
    private Instant expiration;

    @Override
    public Map<String, String> getKeyValues() {
        return keyValueMap;
    }
}
