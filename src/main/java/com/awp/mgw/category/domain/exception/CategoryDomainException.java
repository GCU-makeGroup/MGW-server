package com.awp.mgw.category.domain.exception;

import com.awp.mgw.global.exception.BusinessException;
import com.awp.mgw.global.exception.constant.Domain;

public class CategoryDomainException extends BusinessException {
    public CategoryDomainException(CategoryErrorCode errorCode) {
        super(Domain.CATEGORY, errorCode);
    }

    public CategoryDomainException(CategoryErrorCode errorCode, String message) {
        super(Domain.CATEGORY, errorCode, message);
    }
}
