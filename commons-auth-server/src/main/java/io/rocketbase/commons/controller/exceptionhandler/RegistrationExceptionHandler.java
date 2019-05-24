package io.rocketbase.commons.controller.exceptionhandler;

import com.google.common.base.Joiner;
import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.exception.AuthErrorCodes;
import io.rocketbase.commons.exception.RegistrationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class RegistrationExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleRegistrationException(HttpServletRequest request, RegistrationException e) {
        ErrorResponse response = new ErrorResponse(AuthErrorCodes.REGISTRATION.getStatus(), translate(request, "error.registration", "Registation not possible"));
        response.setFields(new HashMap<>());
        if (e.getUsernameErrors() != null && !e.getUsernameErrors().isEmpty()) {
            response.getFields().put("username", Joiner.on(", ").join(e.getUsernameErrors()));
        }
        if (e.getPasswordErrors() != null && !e.getPasswordErrors().isEmpty()) {
            response.getFields().put("password", Joiner.on(", ").join(e.getPasswordErrors()));
        }
        if (e.getEmailErrors() != null && !e.getEmailErrors().isEmpty()) {
            response.getFields().put("email", Joiner.on(", ").join(e.getEmailErrors()));
        }
        return response;
    }
}
