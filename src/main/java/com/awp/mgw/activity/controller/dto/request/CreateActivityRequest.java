package com.awp.mgw.activity.controller.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.List;

public record CreateActivityRequest(
    @NotBlank(message = "활동 제목은 필수입니다.")
    String title,

    @NotNull(message = "카테고리 목록은 필수입니다.")
    List<@NotNull(message = "카테고리 ID는 필수입니다.") @Min(value = 1, message = "카테고리 ID는 1 이상이어야 합니다.") Long> categoryIds,

    @NotNull(message = "최대 인원은 필수입니다.")
    @Min(value = 1, message = "최대 인원은 1 이상이어야 합니다.")
    Integer maxMembers,

    @NotNull(message = "활동 일정은 필수입니다.")
    OffsetDateTime schedule,

    @NotBlank(message = "활동 설명은 필수입니다.")
    String description,

    @NotBlank(message = "오픈채팅 URL은 필수입니다.")
    String openchatUrl,

    @NotBlank(message = "썸네일 URL은 필수입니다.")
    String thumbnailUrl,

    @NotBlank(message = "활동 위치는 필수입니다.")
    String location
) {
}
