package com.awp.mgw.recruitmentgroup.domain.exception;

import com.awp.mgw.global.response.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RecruitmentGroupErrorCode implements BaseCode {

    RECRUITMENT_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "RECRUITMENT-GROUP-001", "모집글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "RECRUITMENT-GROUP-002", "댓글을 찾을 수 없습니다."),

    INVALID_RECRUITMENT_GROUP_TITLE(HttpStatus.BAD_REQUEST, "RECRUITMENT-GROUP-003", "모집글 제목이 유효하지 않습니다."),
    INVALID_RECRUITMENT_GROUP_CONTENT(HttpStatus.BAD_REQUEST, "RECRUITMENT-GROUP-004", "모집글 내용이 유효하지 않습니다."),
    INVALID_RECRUITMENT_GROUP_IMAGE_URL(HttpStatus.BAD_REQUEST, "RECRUITMENT-GROUP-005", "모집글 이미지 URL이 유효하지 않습니다."),
    INVALID_RECRUITMENT_GROUP_AUTHOR(HttpStatus.BAD_REQUEST, "RECRUITMENT-GROUP-006", "모집글 작성자 정보가 유효하지 않습니다."),
    INVALID_COMMENT_CONTENT(HttpStatus.BAD_REQUEST, "RECRUITMENT-GROUP-007", "댓글 내용이 유효하지 않습니다."),
    INVALID_COMMENT_PARENT(HttpStatus.BAD_REQUEST, "RECRUITMENT-GROUP-008", "부모 댓글 정보가 유효하지 않습니다."),

    RECRUITMENT_GROUP_NOT_OWNED(HttpStatus.FORBIDDEN, "RECRUITMENT-GROUP-009", "본인의 모집글만 수정/삭제할 수 있습니다."),
    COMMENT_NOT_OWNED(HttpStatus.FORBIDDEN, "RECRUITMENT-GROUP-010", "본인의 댓글만 수정/삭제할 수 있습니다."),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
