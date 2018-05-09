package com.timazet.controller;

import com.timazet.controller.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.UndeclaredThrowableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.InvocationTargetException;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    /**
     * Treats special case for CGLIB Proxy and invocation target, which throws exception during the invocation
     */
    @ExceptionHandler(UndeclaredThrowableException.class)
    public ResponseEntity<ErrorResponse> handle(UndeclaredThrowableException exception) {
        Throwable toHandle = exception.getUndeclaredThrowable();
        if (toHandle instanceof InvocationTargetException && toHandle.getCause() != null) {
            toHandle = toHandle.getCause();
        }
        return handle(toHandle);
    }

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
