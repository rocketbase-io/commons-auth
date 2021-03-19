package io.rocketbase.commons.dto.appteam;

import io.rocketbase.commons.model.HasKeyValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppTeamWrite implements Serializable, HasKeyValue {

    @NotNull
    @Size(min = 3, max = 100)
    private String name;

    @Nullable
    @Size(max = 512)
    private String description;

    private boolean personal;

    @Nullable
    private Map<String, String> keyValues;
}