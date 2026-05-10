package com.awp.mgw.activity.usecase;

import com.awp.mgw.activity.controller.dto.response.ActivityDetailResponse;

public interface GetActivityDetailUseCase {
    ActivityDetailResponse getActivityDetail(Long memberId, Long activityId);
}
