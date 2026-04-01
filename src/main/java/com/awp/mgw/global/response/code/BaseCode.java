package com.awp.mgw.global.response.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {
    HttpStatus getHttpStatus();

    String getCode();

    String getMessage();
}
