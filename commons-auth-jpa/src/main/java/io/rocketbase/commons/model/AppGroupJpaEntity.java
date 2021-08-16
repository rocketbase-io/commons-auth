package io.rocketbase.commons.model;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@NamedEntityGraph(
        name = "co-group-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("parent"),
                @NamedAttributeNode("capabilities"),
                @NamedAttributeNode("keyValues")
        }
)
@Entity
@Table(name = "co_group",
        uniqueConstraints = @UniqueConstraint(name = "uk_group", columnNames = {"name", "parent_id"}),
        indexes = {
                @Index(name = "idx_group_systemrefid", columnList = "system_ref_id"),
                @Index(name = "idx_group_parent", columnList = "parent_id"),
        }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AppGroupJpaEntity implements AppGroupEntity {

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

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_group__parent"))
    private AppGroupJpaEntity parent;

    @Transient
    private Long parentHolder;

    @Transient
    public void setParentId(Long id) {
        parentHolder = id;
    }

    @Transient
    public Long getParentId() {
        return parent == null ? null : parent.getId();
    }

    @NotNull
    @Column(name = "name_path", length = 1009)
    private String namePath;

    @Column(name = "with_children")
    private boolean withChildren;

    @ManyToMany
    @JoinTable(
            name = "co_group_capability",
            joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_group_capability__group")),
            uniqueConstraints = @UniqueConstraint(name = "uk_group_capability", columnNames = {"group_id", "capability_id"}),
            inverseJoinColumns = @JoinColumn(name = "capability_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_group_capability__capability")),
            indexes = {
                    @Index(name = "idx_group_capability_c", columnList = "capability_id"),
                    @Index(name = "idx_group_capability_g", columnList = "group_id"),
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

    @ElementCollection
    @CollectionTable(
            name = "co_group_keyvalue",
            joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_group_keyvalue__group")),
            uniqueConstraints = @UniqueConstraint(name = "uk_user_keyvalue", columnNames = {"group_id", "field_key"}),
            indexes = {
                    @Index(name = "idx_group_keyvalue_g", columnList = "group_id"),
                    @Index(name = "idx_group_keyvalue_v", columnList = "field_key, field_value")},
            foreignKey = @ForeignKey(name = "fk_group__keyvalue")
    )
    @MapKeyColumn(name = "field_key", length = 50)
    @Column(name = "field_value", length = 255, nullable = false)
    @Builder.Default
    private Map<String, String> keyValues = new HashMap<>();

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

    public AppGroupJpaEntity(Long id) {
        this.id = id;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AppGroupEntity)) return false;
        final AppGroupEntity other = (AppGroupEntity) o;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AppGroupJpaEntity{" +
                "id=" + id +
                ", systemRefId='" + systemRefId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", parentId=" + (parent != null ? parent.getId() : null) +
                ", namePath='" + namePath + '\'' +
                ", withChildren=" + withChildren +
                ", keyValues=" + keyValues +
                ", created=" + created +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", modified=" + modified +
                '}';
    }
}
