package io.rocketbase.commons.test.model;

import io.rocketbase.commons.dto.appteam.AppTeamInvite;
import io.rocketbase.commons.model.AppInviteEntity;
import lombok.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleAppInvite implements AppInviteEntity {

    private Long id;

    private String invitor;

    private String message;

    private String firstName;

    private String lastName;

    private String email;

    private Set<Long> capabilityIds;

    @Singular
    private Map<String, String> keyValues = new HashMap<>();

    private Set<Long> groupIds;

    private Instant expiration;

    private Instant created;

    private AppTeamInvite teamInvite;
}
