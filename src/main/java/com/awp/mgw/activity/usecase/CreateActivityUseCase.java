package com.awp.mgw.activity.usecase;

import com.awp.mgw.activity.controller.dto.request.CreateActivityRequest;
import com.awp.mgw.activity.controller.dto.response.ActivityIdResponse;

public interface CreateActivityUseCase {
    ActivityIdResponse createActivity(Long memberId, CreateActivityRequest request);
}
