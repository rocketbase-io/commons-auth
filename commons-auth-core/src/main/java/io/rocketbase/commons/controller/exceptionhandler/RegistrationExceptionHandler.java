package io.rocketbase.commons.controller.exceptionhandler;

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
        ErrorResponse response = new ErrorResponse(AuthErrorCodes.REGISTRATION_ALREADY_IN_USE.getStatus(), translate(request, "error.registration", "Username/Email already used"));
        response.setFields(new HashMap<>());
        if (e.isUsername()) {
            response.getFields().put("username", translate(request, "error.registration.username", "Username already used"));
        }
        if (e.isEmail()) {
            response.getFields().put("email", translate(request, "error.registration.email", "Email already used"));
        }
        return response;
    }
}
