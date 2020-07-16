package io.rocketbase.commons.service.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class EmailAddress {

    @NotNull
    @Email
    private final String email;

    @Nullable
    private final String name;

    public EmailAddress(String email) {
        this.email = email;
        this.name = null;
    }

    public boolean isValid() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Set<ConstraintViolation<EmailAddress>> validate = factory.getValidator().validate(this);
        return validate == null || validate.isEmpty();
    }
}
