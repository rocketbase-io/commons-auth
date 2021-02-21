package io.rocketbase.commons.model;

import io.rocketbase.commons.model.embedded.UserProfileJpaEmbedded;
import io.rocketbase.commons.model.embedded.UserSettingJpaEmbedded;
import io.rocketbase.commons.model.user.UserProfile;
import io.rocketbase.commons.model.user.UserSetting;
import io.rocketbase.commons.service.user.AppUserJpaPersistenceService;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Entity
@Table(name = "co_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_username", columnNames = {"username"}),
                @UniqueConstraint(name = "uk_user_email", columnNames = {"email"})},
        indexes = {@Index(name = "idx_user_systemrefid", columnList = "system_ref_id")})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AppUserJpaEntity implements AppUserEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Nullable
    @Column(name = "system_ref_id", length = 100)
    private String systemRefId;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    @Email
    private String email;

    @ManyToMany
    @JoinTable(
            name = "co_user_capability",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_user_capability__user")),
            uniqueConstraints = @UniqueConstraint(name = "uk_user_capability", columnNames = {"user_id", "capability_id"}),
            inverseJoinColumns = @JoinColumn(name = "capability_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_user_capability__capability")),
            indexes = {
                    @Index(name = "idx_user_capability_c", columnList = "capability_id"),
                    @Index(name = "idx_user_capability_u", columnList = "user_id"),
            }
    )
    private Set<AppCapabilityJpaEntity> capabilities;

    /**
     * used only to keep api compatible with mongo<br>
     * {@link AppUserJpaPersistenceService} take care of holder and transpiles it to capabilities
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
            name = "co_user_keyvalue",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_user_keyvalue__user")),
            uniqueConstraints = @UniqueConstraint(name = "uk_user_keyvalue", columnNames = {"user_id", "field_key"}),
            indexes = {
                    @Index(name = "idx_user_keyvalue_user", columnList = "user_id"),
                    @Index(name = "idx_user_keyvalue_value", columnList = "field_key, field_value"),
            }
    )
    @MapKeyColumn(name = "field_key", length = 50)
    @Column(name = "field_value", length = 255, nullable = false)
    @Builder.Default
    private Map<String, String> keyValues = new HashMap<>();

    @ManyToMany
    @JoinTable(name = "co_user_group",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_user_group__user")),
            uniqueConstraints = @UniqueConstraint(name = "uk_user_group", columnNames = {"user_id", "group_id"}),
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_user_group__group")),
            indexes = {
                    @Index(name = "idx_user_group_g", columnList = "group_id"),
                    @Index(name = "idx_user_group_u", columnList = "user_id"),
            }
    )
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_user__team"))
    private AppTeamJpaEntity activeTeam;

    /**
     * used only to keep api compatible with mongo<br>
     * {@link AppUserJpaPersistenceService} take care of holder and transpiles it to activeTeam
     */
    @Transient
    private Long activeTeamHolder;

    @Transient
    public void setActiveTeamId(Long id) {
        activeTeamHolder = id;
    }

    @Transient
    public Long getActiveTeamId() {
        return activeTeam == null ? null : activeTeam.getId();
    }

    private boolean enabled;

    private boolean locked;

    @NotNull
    private Instant created;

    @Column(name = "last_login")
    private Instant lastLogin;

    @Column(name = "last_token_invalidation")
    private Instant lastTokenInvalidation;

    public void updateLastLogin() {
        this.lastLogin = Instant.now();
    }

    public void updateLastTokenInvalidation() {
        this.lastTokenInvalidation = Instant.now();
    }

    @Embedded
    private UserProfileJpaEmbedded profile;

    public void setProfile(UserProfile userProfile) {
        this.profile = new UserProfileJpaEmbedded(userProfile);
    }

    @Embedded
    private UserSettingJpaEmbedded setting;

    public void setSetting(UserSetting userSetting) {
        this.setting = new UserSettingJpaEmbedded(userSetting);
    }

    public AppUserJpaEntity(String id) {
        this.id = id;
    }

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (created == null) {
            created = Instant.now();
        }
    }
}
