package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

public interface AppGroupEntity extends Serializable, EntityWithKeyValue<AppGroupEntity>, EntityWithAudit, EntityWithSystemRefId {

    Long getId();

    void setId(Long id);

    String getName();

    void setName(@NotNull
                 @Pattern(regexp = "^[^\\n^\\/]*$")
                 @Size(min = 1, max = 100) String name);

    String getDescription();

    void setDescription(@Size(max = 500) String description);

    Set<Long> getCapabilityIds();

    void setCapabilityIds(Set<Long> capabilityIds);

    /**
     * root group's parent is it's own id
     */
    Long getParentId();

    void setParentId(Long parentId);

    /**
     * calculated tree of parent's names to get also parent names<br>
     * example: /buyers/department/manager
     */
    String getNamePath();

    void setNamePath(@NotNull @Size(min = 1, max = 1009) String treePath);

    /**
     * is group parent for any other group in the database
     */
    boolean isWithChildren();

    void setWithChildren(boolean withChildren);

    default int getDepth() {
        return AppGroupRead.ROOT.getNamePath().equals(getNamePath()) ? 0 : StringUtils.countOccurrencesOf(getNamePath(), "/");
    }

    default long getSortOrder() {
        return getDepth() * 100000000000000L + getCreated().toEpochMilli();
    }

}
