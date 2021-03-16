package io.rocketbase.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "co_capacity",
        uniqueConstraints = @UniqueConstraint(name = "uk_capacity",  columnNames = {"key_", "parent_id"}),
        indexes = @Index(name = "idx_capability_parent", columnList = "parent_id")
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppCapabilityJpaEntity implements AppCapabilityEntity {

    @Id
    private Long id;

    @Column(name = "key_", length = 20)
    private String key;

    @Column(length = 500)
    private String description;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_capacity__parent"))
    private AppCapabilityJpaEntity parent;

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
    @Size(min = 1, max = 369)
    @Column(name = "key_path", length = 369)
    private String keyPath;

    @Column(name = "with_children")
    private boolean withChildren;

    private Instant created;

    public AppCapabilityJpaEntity(Long id) {
        this.id = id;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AppCapabilityEntity)) return false;
        final AppCapabilityEntity other = (AppCapabilityEntity) o;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
