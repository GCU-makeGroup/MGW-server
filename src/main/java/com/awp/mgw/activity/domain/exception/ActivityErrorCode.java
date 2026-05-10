package com.awp.mgw.activity.domain.exception;

import com.awp.mgw.global.response.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ActivityErrorCode implements BaseCode {

    ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "ACTIVITY-001", "활동을 찾을 수 없습니다."),

    INVALID_ACTIVITY_TITLE(HttpStatus.BAD_REQUEST, "ACTIVITY-002", "활동 제목이 유효하지 않습니다."),
    INVALID_ACTIVITY_SUBTITLE(HttpStatus.BAD_REQUEST, "ACTIVITY-003", "활동 부제목이 유효하지 않습니다."),
    INVALID_ACTIVITY_DESCRIPTION(HttpStatus.BAD_REQUEST, "ACTIVITY-004", "활동 설명이 유효하지 않습니다."),
    INVALID_ACTIVITY_MAX_MEMBER(HttpStatus.BAD_REQUEST, "ACTIVITY-005", "활동 최대 인원 수가 유효하지 않습니다."),
    INVALID_ACTIVITY_THUMBNAIL_URL(HttpStatus.BAD_REQUEST, "ACTIVITY-006", "활동 썸네일 URL이 유효하지 않습니다."),
    INVALID_ACTIVITY_SCHEDULE(HttpStatus.BAD_REQUEST, "ACTIVITY-007", "활동 일정이 유효하지 않습니다."),
    INVALID_ACTIVITY_LOCATION(HttpStatus.BAD_REQUEST, "ACTIVITY-008", "활동 장소가 유효하지 않습니다."),
    INVALID_ACTIVITY_OPENCHAT_URL(HttpStatus.BAD_REQUEST, "ACTIVITY-009", "오픈채팅 URL이 유효하지 않습니다."),

    ACTIVITY_GROUP_NOT_FOUND(HttpStatus.BAD_REQUEST, "ACTIVITY-010", "활동에 연결된 그룹을 찾을 수 없습니다."),
    ACTIVITY_CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "ACTIVITY-011", "활동에 연결된 카테고리를 찾을 수 없습니다."),
    ACTIVITY_LIKE_NOT_FOUND(HttpStatus.BAD_REQUEST, "ACTIVITY-012", "활동 좋아요 정보를 찾을 수 없습니다."),
    INVALID_ACTIVITY_GROUP_STATUS(HttpStatus.BAD_REQUEST, "ACTIVITY-013", "활동 그룹 상태값이 유효하지 않습니다."),
    FORBIDDEN_ACTIVITY_ACCESS(HttpStatus.FORBIDDEN, "ACTIVITY-014", "활동에 대한 권한이 없습니다."),
    ACTIVITY_CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, "ACTIVITY-015", "활동 정원이 가득 찼습니다."),
    DUPLICATE_ACTIVITY_MEMBER_JOIN(HttpStatus.CONFLICT, "ACTIVITY-016", "이미 활동에 참여 중인 사용자입니다."),
    DUPLICATE_ACTIVITY_GROUP_JOIN(HttpStatus.CONFLICT, "ACTIVITY-017", "이미 활동에 참여 중인 그룹입니다."),
    MEMBER_ID_REQUIRED_FOR_SCOPE(HttpStatus.BAD_REQUEST, "ACTIVITY-018", "joined/created 조회에는 memberId가 필요합니다."),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
