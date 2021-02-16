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


@Document(collection = "invite")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AppInviteMongoEntity implements AppInviteEntity<Long,Long> {

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

    private Set<Long> capabilities;

    private Set<Long> groups;

    @CreatedDate
    private Instant created;

    @Indexed
    private Instant expiration;

    @Builder.Default
    private Map<String, String> keyValues = new HashMap<>();

    private AppTeamInvite teamInvite;

}
