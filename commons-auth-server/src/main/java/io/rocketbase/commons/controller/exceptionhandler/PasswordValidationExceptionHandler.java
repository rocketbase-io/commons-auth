package io.rocketbase.commons.controller.exceptionhandler;

import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.exception.ErrorCodes;
import io.rocketbase.commons.exception.PasswordValidationException;
import io.rocketbase.commons.exception.ValidationErrorCode;
import io.rocketbase.commons.util.Nulls;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class PasswordValidationExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handlePasswordValidationException(HttpServletRequest request, PasswordValidationException e) {
        ErrorResponse response = new ErrorResponse(ErrorCodes.FORM_ERROR.getStatus(), translate(request, "auth.error.passwordValidation", "Password not fitting requirements"));
        if (e.getErrors() != null) {
            for (ValidationErrorCode<PasswordErrorCodes> c : e.getErrors()) {
                response.addField(c.getField(), Nulls.notNull(c.getMessage(), c.getCode().getValue()));
            }
        }
        return response;
    }
}
