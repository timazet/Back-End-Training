package com.timazet.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import java.time.ZonedDateTime;

public class ErrorResponse {

    @JsonFormat(pattern = StdDateFormat.DATE_FORMAT_STR_ISO8601)
    private ZonedDateTime timestamp;
    private String message;

    public ErrorResponse() {

    }

    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = ZonedDateTime.now();
    }

    @Override
    public String toString() {
        return "ErrorResponse(timestamp=" + this.getTimestamp() + ", message=" + this.getMessage() + ")";
    }

    public ZonedDateTime getTimestamp() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message;
    }

}
