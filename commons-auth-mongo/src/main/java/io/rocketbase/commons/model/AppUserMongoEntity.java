package io.rocketbase.commons.model;

import io.rocketbase.commons.model.user.UserProfile;
import io.rocketbase.commons.model.user.UserSetting;
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


@Document(collection = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AppUserMongoEntity implements AppUserEntity<Long,Long, Long> {

    @Id
    private String id;

    @Indexed
    private String systemRefId;

    @NotNull
    @Indexed(unique = true)
    private String username;

    @NotNull
    private String password;

    @NotNull
    @Indexed(unique = true)
    @Email
    private String email;

    private UserProfile profile;

    private UserSetting setting;

    private Set<Long> capabilities;

    private Set<Long> groups;

    private Long activeTeam;

    private boolean enabled;

    @CreatedDate
    private Instant created;

    private Instant lastLogin;

    private Instant lastTokenInvalidation;

    @Builder.Default
    private Map<String, String> keyValues = new HashMap<>();

    public void updateLastLogin() {
        this.lastLogin = Instant.now();
    }

    @Override
    public void updateLastTokenInvalidation() {
        this.lastTokenInvalidation = Instant.now();
    }
}
