package io.rocketbase.commons.model;

import io.rocketbase.commons.model.converter.SetStringConverter;
import io.rocketbase.commons.service.invite.AppInviteJpaPersistenceService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@NamedEntityGraph(
        name = "co-client-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("capabilities")
        }
)
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
@EntityListeners(AuditingEntityListener.class)
public class AppClientJpaEntity implements AppClientEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Nullable
    @Column(name = "system_ref_id", length = 100)
    private String systemRefId;

    @NotNull
    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", length = 500)
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

    /**
     * used only to keep api compatible with mongo<br>
     * {@link AppInviteJpaPersistenceService} take care of holder and transpiles it to groups
     */
    @Transient
    private Set<Long> capabilityHolder;

    @Transient
    public void setCapabilityIds(Set<Long> ids) {
        capabilityHolder = ids;
    }

    @Transient
    public Set<Long> getCapabilityIds() {
        return capabilities == null ? null : capabilities.stream().map(AppCapabilityJpaEntity::getId).collect(Collectors.toSet());
    }

    @Lob
    @Column(name = "redirect_url_json")
    @Convert(converter = SetStringConverter.class)
    private Set<String> redirectUrls;

    @NotNull
    @CreatedDate
    @Column(name = "created")
    private Instant created;

    @LastModifiedBy
    @Column(name = "modified_by", length = 36)
    private String modifiedBy;

    @NotNull
    @LastModifiedDate
    @Column(name = "modified")
    private Instant modified;

    public AppClientJpaEntity(Long id) {
        this.id = id;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AppClientEntity)) return false;
        final AppClientEntity other = (AppClientEntity) o;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AppClientJpaEntity{" +
                "id=" + id +
                ", systemRefId='" + systemRefId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", redirectUrls=" + redirectUrls +
                ", created=" + created +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", modified=" + modified +
                '}';
    }
}
