package com.awp.mgw.mypage.controller.dto.request;

import com.awp.mgw.member.domain.enums.Language;
import jakarta.validation.constraints.NotNull;

public record UpdateAppLanguageRequest(
        @NotNull Language appLanguage
) {
}
