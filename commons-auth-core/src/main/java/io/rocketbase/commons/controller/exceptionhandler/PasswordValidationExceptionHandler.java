package io.rocketbase.commons.controller.exceptionhandler;

import com.google.common.base.Joiner;
import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.exception.ErrorCodes;
import io.rocketbase.commons.exception.PasswordValidationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class PasswordValidationExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handlePasswordValidationException(HttpServletRequest request, PasswordValidationException e) {
        ErrorResponse response = new ErrorResponse(ErrorCodes.FORM_ERROR.getStatus(), translate(request, "error.passwordValidation", "Password not fitting requirements"));
        response.setFields(new HashMap<>());
        response.getFields().put("password", Joiner.on(", ").join(e.getErrors()));
        return response;
    }
}
