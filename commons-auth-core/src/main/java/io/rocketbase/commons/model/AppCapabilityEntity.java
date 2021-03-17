package io.rocketbase.commons.model;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

public interface AppCapabilityEntity extends Serializable {

    Long getId();

    void setId(Long id);

    String getKey();

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]+$")
    @Size(min = 1, max = 36)
    void setKey(String key);

    String getDescription();

    @Nullable
    @Size(max = 500)
    void setDescription(String description);

    /**
     * root capability's parent is it's own id
     */
    Long getParentId();

    void setParentId(Long parentId);

    /**
     * calculated path<br>
     * concat each parent0.parent1.key split by .
     */
    String getKeyPath();

    @NotNull
    @Size(min = 1, max = 369)
    void setKeyPath(String keyPath);

    /**
     * is capability parent for any capability in the database
     */
    boolean isWithChildren();

    void setWithChildren(boolean withChildren);

    Instant getCreated();

    /**
     * calculated parent count
     * <ul>
     *     <li>parent0.parent1.key has depth 2</li>
     *     <li>key has depth 0</li>
     * </ul>
     */
    default int getDepth() {
        return StringUtils.countOccurrencesOf(getKeyPath(), ".");
    }



}
