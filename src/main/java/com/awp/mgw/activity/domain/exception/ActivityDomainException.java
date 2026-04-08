package com.awp.mgw.activity.domain.exception;

import com.awp.mgw.global.exception.BusinessException;
import com.awp.mgw.global.exception.constant.Domain;

public class ActivityDomainException extends BusinessException {
    public ActivityDomainException(ActivityErrorCode errorCode) {
        super(Domain.ACTIVITY, errorCode);
    }

    public ActivityDomainException(ActivityErrorCode errorCode, String message) {
        super(Domain.ACTIVITY, errorCode, message);
    }
}
