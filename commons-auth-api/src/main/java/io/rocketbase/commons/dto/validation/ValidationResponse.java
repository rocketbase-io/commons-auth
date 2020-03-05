package io.rocketbase.commons.dto.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Joiner;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UsernameErrorCodes.class, name = "username"),
        @JsonSubTypes.Type(value = PasswordErrorCodes.class, name = "password"),
        @JsonSubTypes.Type(value = EmailErrorCodes.class, name = "email"),
        @JsonSubTypes.Type(value = TokenErrorCodes.class, name = "token")})
public class ValidationResponse<T extends Enum> implements Serializable {

    private boolean valid;

    @Singular
    private Map<T, String> errorCodes;

    @JsonIgnore
    public String getMessage(String separator) {
        if (!errorCodes.isEmpty()) {
            return Joiner.on(separator).join(errorCodes.values());
        }
        return null;
    }
}
