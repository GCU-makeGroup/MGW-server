package com.awp.mgw.activity.usecase;

import com.awp.mgw.activity.controller.dto.response.ActivityIdResponse;

public interface DeleteActivityUseCase {
    ActivityIdResponse deleteActivity(Long memberId, Long activityId);
}
