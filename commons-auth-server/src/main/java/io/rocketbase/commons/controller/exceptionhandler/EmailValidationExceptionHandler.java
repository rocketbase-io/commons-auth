package io.rocketbase.commons.controller.exceptionhandler;

import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.dto.validation.EmailErrorCodes;
import io.rocketbase.commons.exception.EmailValidationException;
import io.rocketbase.commons.exception.ErrorCodes;
import io.rocketbase.commons.exception.ValidationErrorCode;
import io.rocketbase.commons.util.Nulls;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class EmailValidationExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleEmailValidationException(HttpServletRequest request, EmailValidationException e) {
        ErrorResponse response = new ErrorResponse(ErrorCodes.FORM_ERROR.getStatus(), translate(request, "auth.error.emailValidation", "Email is used or incorrect"));
        if (e.getErrors() != null) {
            for (ValidationErrorCode<EmailErrorCodes> c : e.getErrors()) {
                response.addField("email", Nulls.notNull(c.getMessage(), c.getCode().getValue()));
            }
        }
        return response;
    }
}
