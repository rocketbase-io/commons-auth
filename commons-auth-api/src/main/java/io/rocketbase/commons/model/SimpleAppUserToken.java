package io.rocketbase.commons.model;

import com.google.common.collect.ImmutableMap;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleAppUserToken implements AppUserToken {

    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String avatar;

    private List<String> roles;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private Map<String, String> keyValueMap = new HashMap<>();

    public SimpleAppUserToken(String id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    @Override
    public Map<String, String> getKeyValues() {
        return getKeyValueMap() != null ? ImmutableMap.copyOf(getKeyValueMap()) : null;
    }
}
