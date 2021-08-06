package io.rocketbase.commons.dto.appcapability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppCapabilityWrite implements Serializable {

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]+$")
    @Size(min = 1, max = 36)
    private String key;

    @Nullable
    @Size(max = 500)
    private String description;

    @Nullable
    private String systemRefId;
}
