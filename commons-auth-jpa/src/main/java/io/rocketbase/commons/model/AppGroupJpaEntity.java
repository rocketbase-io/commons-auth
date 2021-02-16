package io.rocketbase.commons.model;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
@EqualsAndHashCode(of = {"id"})
public class AppGroupJpaEntity implements AppGroupEntity {

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

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_group__parent"))
    private AppGroupJpaEntity parent;

    @Transient
    private Long parentId;

    @Transient
    public Long getParentId() {
        return parent == null ? null : parent.getId();
    }

    @NotNull
    @Size(min = 1, max = 1009)
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

    @Setter
    @Transient
    private Set<Long> capabilityIds;

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
    private Instant created;

    public AppGroupJpaEntity(Long id) {
        this.id = id;
    }

}
