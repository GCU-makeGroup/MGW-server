package com.awp.mgw.group.domain.exception;

import com.awp.mgw.global.exception.BusinessException;
import com.awp.mgw.global.exception.constant.Domain;

public class GroupDomainException extends BusinessException {
    public GroupDomainException(GroupErrorCode errorCode) {
        super(Domain.GROUP, errorCode);
    }

    public GroupDomainException(GroupErrorCode errorCode, String message) {
        super(Domain.GROUP, errorCode, message);
    }
}
