package com.awp.mgw.common.exception;

import com.awp.mgw.global.exception.BusinessException;
import com.awp.mgw.global.exception.constant.CommonErrorCode;
import com.awp.mgw.global.exception.constant.Domain;

public class CommonException extends BusinessException {
    public CommonException(CommonErrorCode errorCode) {
        super(Domain.COMMON, errorCode);
    }

    public CommonException(CommonErrorCode errorCode, String message) {
        super(Domain.COMMON, errorCode, message);
    }
}
