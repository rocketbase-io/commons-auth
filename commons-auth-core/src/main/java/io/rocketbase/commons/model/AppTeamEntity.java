package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.appteam.AppTeamRole;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public interface AppTeamEntity extends Serializable, EntityWithKeyValue<AppTeamEntity> {

    Long getId();

    void setId(Long id);

    String getName();

    void setName(@Size(max = 100) String name);

    String getSystemRefId();

    void setSystemRefId(@Size(max = 100) String systemRefId);

    String getDescription();

    void setDescription(@Size(max = 500) String description);

    boolean isPersonal();

    void setPersonal(boolean personal);

    /**
     * key: user-id, value: role
     */
    @Nullable
    Map<String, AppTeamRole> getMembers();

    void setMembers(Map<String, AppTeamRole> members);

    Instant getCreated();

    Instant getModified();

    String getModifiedBy();

}
