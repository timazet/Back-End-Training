package com.timazet.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;

@ToString
@Getter
@NoArgsConstructor
public class ErrorResponse {

    @JsonFormat(pattern = StdDateFormat.DATE_FORMAT_STR_ISO8601)
    private ZonedDateTime timestamp;

    private String message;

    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = ZonedDateTime.now();
    }

}
