package com.awp.mgw.activity.usecase;

import com.awp.mgw.activity.controller.dto.response.ActivityIdResponse;

public interface LikeActivityUseCase {
    ActivityIdResponse likeActivity(Long memberId, Long activityId);
}
