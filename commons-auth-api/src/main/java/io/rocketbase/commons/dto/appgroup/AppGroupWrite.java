package io.rocketbase.commons.dto.appgroup;

import io.rocketbase.commons.model.HasKeyValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppGroupWrite implements Serializable, HasKeyValue {

    @NotNull
    @Pattern(regexp = "^[^\\n^\\/]*$")
    @Size(min = 1, max = 100)
    private String name;

    @Nullable
    private String systemRefId;

    @Size(max = 512)
    private String description;

    private Set<Long> capabilityIds;

    @Nullable
    private Map<String, String> keyValues;
}
