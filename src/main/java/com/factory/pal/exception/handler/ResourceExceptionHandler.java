package com.factory.pal.exception.handler;

import com.factory.pal.exception.MachineKeyInvalidException;
import com.factory.pal.exception.ParameterValueInvalidException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(annotations = RestController.class)
public class ResourceExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {MachineKeyInvalidException.class})
    protected ResponseEntity<Object> handleMachineKeyInvalidException(MachineKeyInvalidException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(),
                new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(value
            = {ParameterValueInvalidException.class})
    protected ResponseEntity<Object> handleParameterValueInvalidException(
            ParameterValueInvalidException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(),
                new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

}
