package com.awp.mgw.category.domain.exception;

import com.awp.mgw.global.response.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CategoryErrorCode implements BaseCode {

    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY-001", "카테고리를 찾을 수 없습니다."),

    INVALID_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "CATEGORY-002", "카테고리명이 유효하지 않습니다."),
    DUPLICATE_CATEGORY_NAME(HttpStatus.CONFLICT, "CATEGORY-003", "이미 존재하는 카테고리명입니다."),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
