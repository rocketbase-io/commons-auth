package io.rocketbase.commons.dto.appuser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserRead implements Serializable {

    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String avatar;

    private List<String> roles;

    private Map<String, String> keyValues;

    private Boolean enabled;

    private LocalDateTime created;

    private LocalDateTime lastLogin;

}
