package com.awp.mgw.member.domain.exception;

import com.awp.mgw.global.response.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-001", "회원을 찾을 수 없습니다."),

    INVALID_LOGIN_INFO(HttpStatus.UNAUTHORIZED, "MEMBER-010", "이메일 또는 비밀번호가 일치하지 않습니다."),

    INVALID_MEMBER_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER-002", "회원 이메일이 유효하지 않습니다."),
    INVALID_MEMBER_NAME(HttpStatus.BAD_REQUEST, "MEMBER-003", "회원 이름이 유효하지 않습니다."),
    INVALID_MEMBER_IMAGE_URL(HttpStatus.BAD_REQUEST, "MEMBER-004", "회원 이미지 URL이 유효하지 않습니다."),
    INVALID_MEMBER_INTRODUCTION(HttpStatus.BAD_REQUEST, "MEMBER-005", "회원 소개가 유효하지 않습니다."),
    INVALID_MEMBER_POINT(HttpStatus.BAD_REQUEST, "MEMBER-006", "회원 포인트 값이 유효하지 않습니다."),
    INVALID_MEMBER_STATUS(HttpStatus.BAD_REQUEST, "MEMBER-007", "회원 상태가 유효하지 않습니다."),

    DUPLICATE_MEMBER_EMAIL(HttpStatus.CONFLICT, "MEMBER-008", "이미 가입된 이메일입니다."),
    MEMBER_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-009", "회원 설정 정보를 찾을 수 없습니다."),

    EMAIL_VERIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-011", "이메일 인증 요청을 찾을 수 없습니다."),
    EMAIL_VERIFICATION_CODE_INVALID(HttpStatus.BAD_REQUEST, "MEMBER-012", "이메일 인증 코드가 올바르지 않습니다."),
    EMAIL_VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "MEMBER-013", "이메일 인증 코드가 만료되었습니다."),
    EMAIL_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "MEMBER-014", "이미 인증된 이메일입니다."),

    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "MEMBER-015", "Refresh Token이 유효하지 않습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "MEMBER-016", "Refresh Token이 만료되었습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "MEMBER-017", "이메일 인증이 완료되지 않았습니다."),

    INVALID_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER-018", "현재 비밀번호가 일치하지 않습니다."),
    MEMBER_WITHDRAWN(HttpStatus.FORBIDDEN, "MEMBER-019", "탈퇴한 회원입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
