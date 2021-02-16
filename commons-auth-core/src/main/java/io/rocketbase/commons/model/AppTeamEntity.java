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

    @Size(max = 100)
    void setName(String name);

    String getSystemRefId();

    @Size(max = 100)
    void setSystemRefId(String systemRefId);

    String getDescription();

    @Size(max = 500)
    void setDescription(String description);

    boolean isPersonal();

    void setPersonal(boolean personal);

    /**
     * key: user-id, value: role
     */
    @Nullable
    Map<String, AppTeamRole> getMembers();

    void setMembers(Map<String, AppTeamRole> members);

    Instant getCreated();

}
