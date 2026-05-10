package com.awp.mgw.activity.controller.dto.response;

public record ActivityIdResponse(Long activityId) {
    public static ActivityIdResponse from(Long activityId) {
        return new ActivityIdResponse(activityId);
    }
}
