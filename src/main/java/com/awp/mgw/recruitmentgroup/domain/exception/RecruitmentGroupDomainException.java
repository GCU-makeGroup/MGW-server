package com.awp.mgw.recruitmentgroup.domain.exception;

import com.awp.mgw.global.exception.BusinessException;
import com.awp.mgw.global.exception.constant.Domain;

public class RecruitmentGroupDomainException extends BusinessException {
    public RecruitmentGroupDomainException(RecruitmentGroupErrorCode errorCode) {
        super(Domain.RECRUITMENT_GROUP, errorCode);
    }

    public RecruitmentGroupDomainException(RecruitmentGroupErrorCode errorCode, String message) {
        super(Domain.RECRUITMENT_GROUP, errorCode, message);
    }
}
