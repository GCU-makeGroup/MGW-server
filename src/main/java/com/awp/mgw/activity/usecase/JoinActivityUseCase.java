package com.awp.mgw.activity.usecase;

import com.awp.mgw.activity.controller.dto.request.JoinActivityRequest;
import com.awp.mgw.activity.controller.dto.response.ActivityIdResponse;

public interface JoinActivityUseCase {
    ActivityIdResponse joinActivity(Long memberId, Long activityId, JoinActivityRequest request);
}
