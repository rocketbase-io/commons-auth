package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.appteam.AppTeamInvite;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.rocketbase.commons.model.AppInviteMongoEntity.COLLECTION_NAME;


@Document(collection = COLLECTION_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AppInviteMongoEntity implements AppInviteEntity {

    public static final String COLLECTION_NAME = "co_invite";

    @Id
    private Long id;

    @Indexed
    private String invitor;

    private String message;

    private String firstName;

    private String lastName;

    @NotNull
    @Email
    private String email;

    private Set<Long> capabilityIds;

    private Set<Long> groupIds;

    @CreatedDate
    private Instant created;

    @Indexed
    private Instant expiration;

    @Builder.Default
    private Map<String, String> keyValues = new HashMap<>();

    private AppTeamInvite teamInvite;

}
