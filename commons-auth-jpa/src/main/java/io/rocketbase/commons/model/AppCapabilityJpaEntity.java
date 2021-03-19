package io.rocketbase.commons.model;

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
@EntityListeners(AuditingEntityListener.class)
public class AppCapabilityJpaEntity implements AppCapabilityEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "key_", length = 20)
    private String key;

    @Column(name = "description", length = 500)
    private String description;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_capacity__parent"))
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

    @Column(name = "key_path", length = 369)
    private String keyPath;

    @Column(name = "with_children")
    private boolean withChildren;

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

    @Override
    public String toString() {
        return "AppCapabilityJpaEntity{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", description='" + description + '\'' +
                ", parentId=" + (parent != null ? parent.getId() : null) +
                ", keyPath='" + keyPath + '\'' +
                ", withChildren=" + withChildren +
                ", created=" + created +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", modified=" + modified +
                '}';
    }
}
