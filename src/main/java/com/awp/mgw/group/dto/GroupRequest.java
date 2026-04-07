package com.awp.mgw.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class GroupRequest {

    private GroupRequest() {
    }

    public record Create(
        @NotBlank(message = "그룹 제목은 비어 있을 수 없습니다.")
        @Size(max = 255, message = "그룹 제목은 255자 이하여야 합니다.")
        String title,

        @NotBlank(message = "그룹 소개는 비어 있을 수 없습니다.")
        String content,

        @NotNull(message = "공개 여부는 필수입니다.")
        Boolean isPublic
    ) {
    }

    public record Update(
        @NotBlank(message = "그룹 제목은 비어 있을 수 없습니다.")
        @Size(max = 255, message = "그룹 제목은 255자 이하여야 합니다.")
        String title,

        @NotBlank(message = "그룹 소개는 비어 있을 수 없습니다.")
        String content,

        @NotNull(message = "공개 여부는 필수입니다.")
        Boolean isPublic
    ) {
    }
}
