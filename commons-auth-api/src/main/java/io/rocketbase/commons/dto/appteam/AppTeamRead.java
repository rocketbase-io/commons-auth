package io.rocketbase.commons.dto.appteam;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppTeamRead extends AppTeamShort implements Serializable {

    private String systemRefId;

    @Nullable
    private String description;

    private boolean personal;

    private Instant created;

    @Nullable
    private Map<String, String> keyValues;

    @Builder
    public AppTeamRead(Long id, String name, String systemRefId, String description, boolean personal, Instant created, Map<String, String> keyValues) {
        super(id, name);
        this.systemRefId = systemRefId;
        this.description = description;
        this.personal = personal;
        this.created = created;
        this.keyValues = keyValues;
    }

    public AppTeamRead(AppTeamRead other) {
        setId(other.getId());
        setName(other.getName());
        this.systemRefId = other.systemRefId;
        this.description = other.description;
        this.personal = other.personal;
        this.created = other.created;
        this.keyValues = other.keyValues != null ? new HashMap<>(other.keyValues) : null;
    }


    @JsonIgnore
    public AppTeamShort toShort() {
        return new AppTeamShort(
                getId(),
                getName()
        );
    }
}
