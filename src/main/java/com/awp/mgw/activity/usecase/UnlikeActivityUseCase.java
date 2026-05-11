package com.awp.mgw.activity.usecase;

import com.awp.mgw.activity.controller.dto.response.ActivityIdResponse;

public interface UnlikeActivityUseCase {
    ActivityIdResponse unlikeActivity(Long memberId, Long activityId);
}
