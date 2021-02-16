package io.rocketbase.commons.dto.authaudit;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AuthAuditRead {

    private Long id;

    private Instant timestamp;

    private String userId;

    /**
     * for example crud / interaction
     */
    private String eventType;

    /**
     * crud: create/read/update/delete<br>
     * interaction: login/refresh_token...
     */
    private String eventDetail;

    /**
     * crud: database-entity-name<br>
     * interaction: service-name/api-endpoint
     */
    private String source;

    /**
     * crud: id of entity<br>
     * interaction: null
     */
    private String identifier;
}
