package com.awp.mgw.activity.controller.dto.response;

public record ActivityImageUploadResponse(String thumbnailUrl) {
    public static ActivityImageUploadResponse from(String thumbnailUrl) {
        return new ActivityImageUploadResponse(thumbnailUrl);
    }
}
