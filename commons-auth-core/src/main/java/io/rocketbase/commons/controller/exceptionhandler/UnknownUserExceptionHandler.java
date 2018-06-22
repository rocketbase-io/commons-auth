package io.rocketbase.commons.controller.exceptionhandler;

import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.exception.AuthErrorCodes;
import io.rocketbase.commons.exception.UnknownUserException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class UnknownUserExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleUnknownUserException(HttpServletRequest request, UnknownUserException e) {
        return new ErrorResponse(AuthErrorCodes.UNKNOWN_USER.getStatus(), translate(request, "error.unknownuser", "User is unknown"));
    }
}
