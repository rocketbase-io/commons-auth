package io.rocketbase.commons.dto.appclient;

import io.rocketbase.commons.dto.appcapability.AppCapabilityShort;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppClientRead implements Serializable {

    private Long id;

    @Nullable
    private String systemRefId;

    private String name;

    private String description;

    private Set<AppCapabilityShort> capabilities;

    private Set<String> redirectUrls;

    private Instant created;

    private String modifiedBy;

    private Instant modified;

}
