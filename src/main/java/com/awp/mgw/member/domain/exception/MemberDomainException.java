package com.awp.mgw.member.domain.exception;

import com.awp.mgw.global.exception.BusinessException;
import com.awp.mgw.global.exception.constant.Domain;

public class MemberDomainException extends BusinessException {
    public MemberDomainException(MemberErrorCode errorCode) {
        super(Domain.MEMBER, errorCode);
    }

    public MemberDomainException(MemberErrorCode errorCode, String message) {
        super(Domain.MEMBER, errorCode, message);
    }
}
