package io.rocketbase.commons.controller.exceptionhandler;

import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.exception.AuthErrorCodes;
import io.rocketbase.commons.exception.EmailDeliveryException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class EmailDeliveryExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleEmailValidationException(HttpServletRequest request, EmailDeliveryException e) {
        return new ErrorResponse(AuthErrorCodes.EMAIL_DELIVERY.getStatus(), translate(request, "auth.error.emailDelivery", "Email delivery not possible"));
    }
}
