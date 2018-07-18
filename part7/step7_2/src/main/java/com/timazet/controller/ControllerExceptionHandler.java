package com.timazet.controller;

import com.timazet.controller.dto.ErrorResponse;
import com.timazet.service.DogNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handle(Throwable throwable) {
        log.error("Error occurred", throwable);

        HttpStatus status;
        if (throwable instanceof IllegalArgumentException || throwable instanceof HttpMediaTypeException
                || throwable instanceof MethodArgumentTypeMismatchException
                || throwable instanceof MethodArgumentNotValidException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (throwable instanceof DogNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity.status(status).body(new ErrorResponse(throwable.getMessage()));
    }

}
