package io.rocketbase.commons.test.model;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.model.AppInviteEntity;
import lombok.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AppInviteTestEntity implements AppInviteEntity {

    private String id;

    private String invitor;

    private String message;

    private String firstName;

    private String lastName;

    private String email;

    private List<String> roles;

    @Builder.Default
    private Map<String, String> keyValueMap = new HashMap<>();


    private Instant created;

    private Instant expiration;

    @Override
    public AppInviteTestEntity clone() {
        Map<String, String> copyedKeyValueMap = getKeyValueMap() != null ? new HashMap<>(ImmutableMap.copyOf(getKeyValueMap())) : null;
        return AppInviteTestEntity.builder()
                .id(getId())
                .invitor(getInvitor())
                .message(getMessage())
                .email(getEmail())
                .roles(getRoles() != null ? getRoles().stream().map(r -> String.valueOf(r)).collect(Collectors.toList()) : null)
                .firstName(getFirstName())
                .lastName(getLastName())
                .created(getCreated())
                .expiration(getExpiration())
                .keyValueMap(copyedKeyValueMap)
                .build();
    }

    @Override
    public Map<String, String> getKeyValues() {
        return keyValueMap;
    }

    @Override
    public void setKeyValues(Map<String, String> map) {
        this.keyValueMap = map;
    }
}
