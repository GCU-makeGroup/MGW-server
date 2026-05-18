package com.awp.mgw.mypage.controller.dto.request;

public record UpdateProfileRequest(
        String name,
        String profileImageUrl
) {
}
