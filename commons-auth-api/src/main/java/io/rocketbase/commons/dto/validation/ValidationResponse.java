package io.rocketbase.commons.dto.validation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UsernameErrorCodes.class, name = "username"),
        @JsonSubTypes.Type(value = PasswordErrorCodes.class, name = "password"),
        @JsonSubTypes.Type(value = EmailErrorCodes.class, name = "email")})
public class ValidationResponse<T extends Enum> implements Serializable {

    private boolean valid;

    private Set<T> errorCodes;
}
