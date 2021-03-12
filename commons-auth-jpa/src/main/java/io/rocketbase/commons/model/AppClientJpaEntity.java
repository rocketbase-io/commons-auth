package io.rocketbase.commons.model;

import io.rocketbase.commons.model.converter.SetStringConverter;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "co_client",
        uniqueConstraints = @UniqueConstraint(name = "uk_client", columnNames = {"name"}),
        indexes = {
                @Index(name = "idx_client_systemrefid", columnList = "system_ref_id"),
        }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AppClientJpaEntity implements AppClientEntity {

    @Id
    private Long id;

    @Nullable
    @Column(name = "system_ref_id", length = 100)
    private String systemRefId;

    @NotNull
    @Column(length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "co_client_capability",
            joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_client_capability__client")),
            uniqueConstraints = @UniqueConstraint(name = "uk_client_capability", columnNames = {"client_id", "capability_id"}),
            inverseJoinColumns = @JoinColumn(name = "capability_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_client_capability__capability")),
            indexes = {
                    @Index(name = "idx_client_capability_c", columnList = "capability_id"),
                    @Index(name = "idx_client_capability_g", columnList = "client_id"),
            }
    )
    private Set<AppCapabilityJpaEntity> capabilities;

    @Setter
    @Transient
    private Set<Long> capabilityIds;

    @Transient
    public Set<Long> getCapabilityIds() {
        return capabilities == null ? null : capabilities.stream().map(AppCapabilityJpaEntity::getId).collect(Collectors.toSet());
    }

    @Lob
    @Column(name = "redirect_url_json")
    @Convert(converter = SetStringConverter.class)
    private Set<String> redirectUrls;

    @NotNull
    private Instant created;

    public AppClientJpaEntity(Long id) {
        this.id = id;
    }

}
