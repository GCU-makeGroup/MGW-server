package com.awp.mgw.activity.usecase;

import com.awp.mgw.activity.controller.dto.request.UpdateActivityRequest;
import com.awp.mgw.activity.controller.dto.response.ActivityIdResponse;

public interface UpdateActivityUseCase {
    ActivityIdResponse updateActivity(Long memberId, Long activityId, UpdateActivityRequest request);
}
