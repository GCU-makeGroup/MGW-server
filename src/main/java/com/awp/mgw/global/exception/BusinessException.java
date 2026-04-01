package com.awp.mgw.global.exception;

import com.awp.mgw.global.exception.constant.Domain;
import com.awp.mgw.global.response.code.BaseCode;
import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {
    private final Domain domain;
    private final BaseCode baseCode;
    private final String message;

    public BusinessException(Domain domain, BaseCode baseCode, String message) {
        super(message != null ? message : baseCode.getMessage());
        this.domain = domain;
        this.baseCode = baseCode;
        this.message = message;
    }

    public BusinessException(Domain domain, BaseCode baseCode) {
        super(baseCode.getMessage());
        this.domain = domain;
        this.baseCode = baseCode;
        this.message = null;
    }
}
