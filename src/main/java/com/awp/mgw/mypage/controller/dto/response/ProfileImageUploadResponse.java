package com.awp.mgw.mypage.controller.dto.response;

public record ProfileImageUploadResponse(String imageUrl) {
    public static ProfileImageUploadResponse from(String imageUrl) {
        return new ProfileImageUploadResponse(imageUrl);
    }
}
