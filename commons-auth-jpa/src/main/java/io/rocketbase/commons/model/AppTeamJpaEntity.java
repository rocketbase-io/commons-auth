package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.appteam.AppTeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "co_team",
        uniqueConstraints = {@UniqueConstraint(name = "uk_team", columnNames = "name")},
        indexes = @Index(name = "idx_team_systemrefid", columnList = "system_ref_id")
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AppTeamJpaEntity implements AppTeamEntity {

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

    @Column(name = "personal")
    private boolean personal;

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

    @ElementCollection
    @CollectionTable(
            name = "co_team_keyvalue",
            joinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "fk_team_keyvalue__team")),
            uniqueConstraints = @UniqueConstraint(name = "uk_team_keyvalue", columnNames = {"team_id", "field_key"}),
            indexes = {
                    @Index(name = "idx_team_keyvalue_t", columnList = "team_id"),
                    @Index(name = "idx_team_keyvalue_v", columnList = "field_key, field_value"),
            }
    )
    @MapKeyColumn(name = "field_key", length = 50)
    @Column(name = "field_value", length = 255, nullable = false)
    @Builder.Default
    private Map<String, String> keyValues = new HashMap<>();

    @ElementCollection
    @CollectionTable(
            name = "co_team_member",
            joinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_team_member__team")),
            uniqueConstraints = @UniqueConstraint(name = "uk_team_member", columnNames = {"team_id", "user_id"}),
            indexes = {
                    @Index(name = "idx_team_member_t", columnList = "team_id"),
                    @Index(name = "idx_team_member_r", columnList = "role"),
                    @Index(name = "idx_team_member_u", columnList = "user_id"),
            }
    )
    @MapKeyColumn(name = "user_id", length = 36)
    @Column(name = "role", length = 10, nullable = false)
    @Builder.Default
    private Map<String, AppTeamRole> members = new HashMap<>();

    public AppTeamJpaEntity(Long id) {
        this.id = id;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AppTeamEntity)) return false;
        final AppTeamEntity other = (AppTeamEntity) o;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AppTeamJpaEntity{" +
                "id=" + id +
                ", systemRefId='" + systemRefId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", personal=" + personal +
                ", created=" + created +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", modified=" + modified +
                ", keyValues=" + keyValues +
                ", members=" + members +
                '}';
    }
}
