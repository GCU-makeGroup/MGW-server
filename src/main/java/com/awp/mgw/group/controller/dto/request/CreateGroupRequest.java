package com.awp.mgw.group.controller.dto.request;

import jakarta.validation.constraints.*;

import java.util.List;

public record CreateGroupRequest(
    @NotBlank(message = "그룹명은 필수입니다.")
    @Size(max = 50, message = "그룹명은 50자 이하여야 합니다.")
    String name,

    @NotBlank(message = "그룹 모집글 제목은 필수입니다.")
    @Size(max = 255, message = "제목은 255자 이하여야 합니다.")
    String title,

    @NotBlank(message = "내용은 필수입니다.")
    String content,

    String imageUrl,

    @NotNull(message = "공개 여부는 필수입니다.")
    Boolean isPublic,

    @NotNull(message = "수용 인원은 필수입니다.")
    @Min(value = 1, message = "수용 인원은 1명 이상이어야 합니다.")
    @Max(value = 20, message = "수용 인원은 20명 이하여야 합니다.")
    Integer capacity,

    @NotEmpty(message = "그룹 카테고리는 최소 1개 이상이어야 합니다.")
    List<
            @NotNull(message = "카테고리 ID는 필수입니다.")
            @Min(value = 1, message = "카테고리 ID는 1 이상이어야 합니다.")
                    Long
            > categoryIds
) {
}
