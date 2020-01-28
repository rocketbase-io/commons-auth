package io.rocketbase.commons.dto.appinvite;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AppInviteRead {
    private String id;

    private String invitor;

    private String message;

    private String firstName;

    private String lastName;

    private String email;

    @Singular
    private List<String> roles;

    @Singular
    private Map<String, String> keyValues;

    private Instant created;

    private Instant expiration;
}
