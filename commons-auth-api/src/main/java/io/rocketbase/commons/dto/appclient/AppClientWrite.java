package io.rocketbase.commons.dto.appclient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppClientWrite implements Serializable {

    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @Nullable
    private String systemRefId;

    @Size(max = 512)
    private String description;

    private Set<Long> capabilityIds;

    private Set<String> redirectUrls;
}
