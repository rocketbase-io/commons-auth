package io.rocketbase.commons.controller.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.rocketbase.commons.exception.OAuthException;
import io.rocketbase.commons.util.Nulls;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class OAuthExceptionHandler extends BaseExceptionHandler {

    @RequiredArgsConstructor
    @Getter
    public static class OAuthErrorResponse {
        private final String error;
        @JsonProperty("error_description")
        private final String message;
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    public OAuthErrorResponse handleEmailValidationException(HttpServletRequest request, OAuthException e) {
        String type = e.getType().name().toLowerCase();
        return new OAuthErrorResponse(type, Nulls.notNull(e.getMessage(), translate(request, String.format("auth.error.%s", type), "OAuth error")));
    }
}
