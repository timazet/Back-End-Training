package com.timazet.controller;

import com.timazet.controller.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handler(final Exception exception) {
        log.error("Error occurred", exception);
        HttpStatus status;
        if (exception instanceof IllegalArgumentException || exception instanceof HttpMediaTypeException || exception instanceof MethodArgumentTypeMismatchException || exception instanceof MethodArgumentNotValidException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (exception instanceof DogNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(status).body(new ErrorResponse(exception.getMessage()));
    }

}
