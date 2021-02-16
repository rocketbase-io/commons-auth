package io.rocketbase.commons.controller.exceptionhandler;

import io.jsonwebtoken.JwtException;
import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.exception.AuthErrorCodes;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ControllerAdvice
public class JwtExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleUnknownUserException(HttpServletRequest request, JwtException e) {
        return new ErrorResponse(AuthErrorCodes.JWT.getStatus(), translate(request, "auth.error.invalidJwt", "Jwt is invalid"));
    }
}
