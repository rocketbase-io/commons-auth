package io.rocketbase.commons.model;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

public interface AppGroupEntity extends Serializable, EntityWithKeyValue<AppGroupEntity> {

    Long getId();

    void setId(Long id);

    String getName();

    void setName(@NotNull
                 @Pattern(regexp = "^[^\\n^\\/]*$")
                 @Size(min = 1, max = 100)
                         String name);

    String getSystemRefId();

    void setSystemRefId(@Nullable
                        @Size(max = 100)
                                String systemRefId);

    String getDescription();

    void setDescription(@Size(max = 500)
                                String description);

    Set<Long> getCapabilityIds();

    void setCapabilityIds(Set<Long> capabilityIds);

    /**
     * root group's parent is it's own id
     */
    Long getParentId();

    void setParentId(Long parentId);

    /**
     * calculated tree of parent's names to get also parent names<br>
     * example: buyers/department/manager
     */
    String getNamePath();

    void setNamePath(@NotNull
                     @Size(min = 1, max = 1009)
                             String treePath);

    /**
     * is group parent for any other group in the database
     */
    boolean isWithChildren();

    void setWithChildren(boolean withChildren);

    Instant getCreated();

    Instant getModified();

    String getModifiedBy();

    default int getDepth() {
        return StringUtils.countOccurrencesOf(getNamePath(), "/");
    }

}
