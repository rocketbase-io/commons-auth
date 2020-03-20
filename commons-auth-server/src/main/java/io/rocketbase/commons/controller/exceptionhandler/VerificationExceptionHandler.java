package io.rocketbase.commons.controller.exceptionhandler;

import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.exception.AuthErrorCodes;
import io.rocketbase.commons.exception.VerificationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class VerificationExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleVerificationException(HttpServletRequest request, VerificationException e) {
        return new ErrorResponse(AuthErrorCodes.VERIFICATION_INVALID.getStatus(), translate(request, "auth.error.verification", "Verification is invalid or expired"))
                .addField(e.getField(), translate(request, "auth.error.verification", "Verification is invalid or expired"));
    }
}
