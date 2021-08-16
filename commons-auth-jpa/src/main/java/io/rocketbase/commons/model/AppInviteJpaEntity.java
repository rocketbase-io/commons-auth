package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.appteam.AppTeamInvite;
import io.rocketbase.commons.model.converter.AppTeamInviteConverter;
import io.rocketbase.commons.service.invite.AppInviteJpaPersistenceService;
import io.rocketbase.commons.service.user.AppUserJpaPersistenceService;
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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@NamedEntityGraph(
        name = "co-invite-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("capabilities"),
                @NamedAttributeNode("keyValues")
        }
)
@Entity
@Table(name = "co_invite",
        indexes = @Index(name = "idx_invite_systemrefid", columnList = "system_ref_id")
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AppInviteJpaEntity implements AppInviteEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Nullable
    @Column(name = "system_ref_id", length = 100)
    private String systemRefId;

    @NotNull
    @Column(name = "invitor")
    private String invitor;

    @Column(name = "message", length = 2000)
    private String message;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @NotNull
    @Email
    @Column(name = "email")
    private String email;

    @ManyToMany
    @JoinTable(
            name = "co_invite_capability",
            joinColumns = @JoinColumn(name = "invite_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_invite_capability__invite")),
            uniqueConstraints = @UniqueConstraint(name = "uk_invite_capability", columnNames = {"invite_id", "capability_id"}),
            inverseJoinColumns = @JoinColumn(name = "capability_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_invite_capability__capability")),
            indexes = {
                    @Index(name = "idx_invite_capability_c", columnList = "capability_id"),
                    @Index(name = "idx_invite_capability_i", columnList = "invite_id"),
            }
    )
    private Set<AppCapabilityJpaEntity> capabilities;

    /**
     * used only to keep api compatible with mongo<br>
     * {@link AppInviteJpaPersistenceService} take care of holder and transpiles it to capabilities
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

    @ElementCollection
    @CollectionTable(
            name = "co_invite_keyvalue",
            joinColumns = @JoinColumn(name = "invite_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_invite_keyvalue__invite")),
            uniqueConstraints = @UniqueConstraint(name = "uk_user_keyvalue", columnNames = {"invite_id", "field_key"}),
            indexes = {
                    @Index(name = "idx_invite_keyvalue_i", columnList = "invite_id"),
                    @Index(name = "idx_invite_keyvalue_v", columnList = "field_key, field_value"),
            },
            foreignKey = @ForeignKey(name = "fk_invite__keyvalue")
    )
    @MapKeyColumn(name = "field_key", length = 50)
    @Column(name = "field_value", length = 255, nullable = false)
    @Builder.Default
    private Map<String, String> keyValues = new HashMap<>();

    @ManyToMany
    @JoinTable(name = "co_invite_group",
            joinColumns = @JoinColumn(name = "invite_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_invite_group__invite")),
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_invite_group__group")),
            indexes = {
                    @Index(name = "idx_invite_group_g", columnList = "group_id"),
                    @Index(name = "idx_invite_group_i", columnList = "invite_id"),
            })
    private Set<AppGroupJpaEntity> groups;

    /**
     * used only to keep api compatible with mongo<br>
     * {@link AppUserJpaPersistenceService} take care of holder and transpiles it to groups
     */
    @Transient
    private Set<Long> groupHolder;

    @Transient
    public void setGroupIds(Set<Long> ids) {
        groupHolder = ids;
    }

    @Transient
    public Set<Long> getGroupIds() {
        return groups == null ? null : groups.stream().map(AppGroupJpaEntity::getId).collect(Collectors.toSet());
    }

    @NotNull
    @Column(name = "expiration")
    private Instant expiration;

    @Column(name = "team_invite", length = 30)
    @Convert(converter = AppTeamInviteConverter.class)
    private AppTeamInvite teamInvite;

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

    public AppInviteJpaEntity(Long id) {
        this.id = id;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AppInviteEntity)) return false;
        final AppInviteEntity other = (AppInviteEntity) o;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AppInviteJpaEntity{" +
                "id=" + id +
                ", invitor='" + invitor + '\'' +
                ", message='" + message + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", keyValues=" + keyValues +
                ", expiration=" + expiration +
                ", teamInvite=" + teamInvite +
                ", created=" + created +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", modified=" + modified +
                '}';
    }
}
