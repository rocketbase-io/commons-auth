package io.rocketbase.commons.controller.exceptionhandler;

import com.google.common.base.Joiner;
import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.exception.AuthErrorCodes;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.exception.ValidationErrorCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class RegistrationExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleRegistrationException(HttpServletRequest request, RegistrationException e) {
        ErrorResponse response = new ErrorResponse(AuthErrorCodes.REGISTRATION.getStatus(), translate(request, "auth.error.registration", "Registation failed"));
        response.setFields(new HashMap<>());
        if (e.getUsernameErrors() != null && !e.getUsernameErrors().isEmpty()) {
            response.getFields().put("username", concatErrors(e.getUsernameErrors()));
        }
        if (e.getPasswordErrors() != null && !e.getPasswordErrors().isEmpty()) {
            response.getFields().put("password", concatErrors(e.getPasswordErrors()));
        }
        if (e.getEmailErrors() != null && !e.getEmailErrors().isEmpty()) {
            response.getFields().put("email", concatErrors(e.getEmailErrors()));
        }
        return response;
    }

    private <T extends Enum<T>> String concatErrors(Set<ValidationErrorCode<T>> errors) {
        if (errors != null) {
            List<String> errorMessages = new ArrayList<>();
            for (ValidationErrorCode<T> c : errors) {
                errorMessages.add(c.getMessage());
            }
            return Joiner.on("; ").join(errorMessages);
        }
        return "";
    }
}
