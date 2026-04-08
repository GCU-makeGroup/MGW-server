package com.awp.mgw.group.domain.exception;

import com.awp.mgw.global.response.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GroupErrorCode implements BaseCode {

    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP-001", "그룹을 찾을 수 없습니다."),

    INVALID_GROUP_TITLE(HttpStatus.BAD_REQUEST, "GROUP-002", "그룹 제목이 유효하지 않습니다."),
    INVALID_GROUP_CONTENT(HttpStatus.BAD_REQUEST, "GROUP-003", "그룹 소개가 유효하지 않습니다."),
    INVALID_GROUP_PUBLIC_STATUS(HttpStatus.BAD_REQUEST, "GROUP-004", "그룹 공개 여부가 유효하지 않습니다."),

    GROUP_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP-005", "그룹 멤버 정보를 찾을 수 없습니다."),
    GROUP_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP-006", "그룹 카테고리 정보를 찾을 수 없습니다."),
    MEMBER_ALREADY_JOINED_GROUP(HttpStatus.CONFLICT, "GROUP-007", "이미 그룹에 가입한 사용자입니다."),
    GROUP_NOT_OWNED(HttpStatus.FORBIDDEN, "GROUP-008", "본인의 그룹만 수정/삭제할 수 있습니다."),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
