package com.awp.mgw.mypage.controller.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateDarkModeRequest(
        @NotNull Boolean darkMode
) {
}
