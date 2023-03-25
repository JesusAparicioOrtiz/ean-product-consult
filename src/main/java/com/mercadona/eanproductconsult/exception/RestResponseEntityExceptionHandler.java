package com.mercadona.eanproductconsult.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseBody
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends RuntimeException {
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidEanException.class)
    protected Error handleInvalidEanException(InvalidEanException e) {
        return new Error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected Error handleValidationExceptions(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());

            return new Error(errors.toString());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataAccessException.class)
    protected Error handleDataAccessException(DataAccessException e) {
        return new Error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected Error handleException(Exception e) {
        return new Error(e.getMessage());
    }
    
}
