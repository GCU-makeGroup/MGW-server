package com.awp.mgw.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class MemberRequest {

    private MemberRequest() {
    }

    public record Create(
        @NotNull(message = "email 값은 필수입니다.")
        Long email,

        @NotBlank(message = "이름은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "이름은 100자 이하여야 합니다.")
        String name,

        String imageUrl,
        String introduction,
        Integer point
    ) {
    }

    public record Update(
        @NotBlank(message = "이름은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "이름은 100자 이하여야 합니다.")
        String name,

        String imageUrl,
        String introduction,
        Integer point
    ) {
    }
}
