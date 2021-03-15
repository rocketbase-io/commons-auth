package io.rocketbase.commons.test.model;

import io.rocketbase.commons.dto.appteam.AppTeamRole;
import io.rocketbase.commons.model.AppTeamEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleAppTeamEntity implements AppTeamEntity {

    private Long id;

    private String name;

    private String systemRefId;

    private String description;

    private boolean personal;

    private Map<String, String> keyValues = new HashMap<>();

    private Map<String, AppTeamRole> members;

    private Instant created;


}


