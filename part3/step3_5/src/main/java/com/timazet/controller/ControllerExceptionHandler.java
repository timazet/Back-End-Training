package com.timazet.controller;

import com.timazet.controller.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handler(final Exception exception) {
        HttpStatus status;
        if (exception instanceof IllegalArgumentException || exception instanceof HttpMediaTypeException
                || exception instanceof MethodArgumentTypeMismatchException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (exception instanceof DogNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity.status(status).body(new ErrorResponse(exception.getMessage()));
    }

}
