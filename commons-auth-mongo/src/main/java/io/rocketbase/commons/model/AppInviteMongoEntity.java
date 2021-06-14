package io.rocketbase.commons.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Document(collection = "invite")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AppInviteMongoEntity implements AppInviteEntity {

    @Id
    private String id;

    @Indexed
    private String invitor;

    private String message;

    private String firstName;

    private String lastName;

    @NotNull
    @Email
    private String email;

    private List<String> roles;

    @CreatedDate
    private Instant created;

    @Indexed
    private Instant expiration;

    @Builder.Default
    private Map<String, String> keyValueMap = new HashMap<>();

    @Override
    public Map<String, String> getKeyValues() {
        return keyValueMap;
    }

    @Override
    public void setKeyValues(Map<String, String> map) {
        this.keyValueMap = map;
    }
}
