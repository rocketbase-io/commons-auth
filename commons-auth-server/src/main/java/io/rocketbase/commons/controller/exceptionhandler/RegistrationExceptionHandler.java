package io.rocketbase.commons.controller.exceptionhandler;

import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.exception.AuthErrorCodes;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.exception.ValidationErrorCode;
import io.rocketbase.commons.util.Nulls;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class RegistrationExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleRegistrationException(HttpServletRequest request, RegistrationException e) {
        ErrorResponse response = new ErrorResponse(AuthErrorCodes.REGISTRATION.getStatus(), translate(request, "auth.error.registration", "Registation failed"));
        addErrors(e.getUsernameErrors(), "username", response);
        addErrors(e.getPasswordErrors(), "password", response);
        addErrors(e.getEmailErrors(), "email", response);
        return response;
    }

    public <T extends Enum<T>> void addErrors(Set<ValidationErrorCode<T>> errors, String path, ErrorResponse response) {
        if (errors != null && !errors.isEmpty()) {
            for (ValidationErrorCode v : errors) {
                response.addField(path, Nulls.notNull(v.getMessage(), v.getCode().name()));
            }
        }
    }
}
