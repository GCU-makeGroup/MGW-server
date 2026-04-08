package com.awp.mgw.category.domain.exception;

import com.awp.mgw.global.response.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CategoryErrorCode implements BaseCode {

    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
