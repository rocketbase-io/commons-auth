package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.openid.ConnectedAuthorization;
import io.rocketbase.commons.model.user.UserProfile;
import io.rocketbase.commons.model.user.UserSetting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static io.rocketbase.commons.model.AppUserMongoEntity.COLLECTION_NAME;


@Document(collection = COLLECTION_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUserMongoEntity implements AppUserEntity {

    public static final String COLLECTION_NAME = "co_user";

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

    private String identityProvider;

    private Set<ConnectedAuthorization> connectedAuthorizations;

    private Set<Long> capabilityIds;

    private Set<Long> groupIds;

    private Long activeTeamId;

    private boolean enabled;

    private boolean locked;

    @NotNull
    @CreatedDate
    private Instant created;

    @LastModifiedBy
    private String modifiedBy;

    @NotNull
    @LastModifiedDate
    private Instant modified;

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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AppUserEntity)) return false;
        final AppUserEntity other = (AppUserEntity) o;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
