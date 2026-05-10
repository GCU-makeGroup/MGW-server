package com.awp.mgw.activity.usecase;

import com.awp.mgw.activity.controller.dto.response.ActivityListResponse;

public interface GetActivityListUseCase {
    ActivityListResponse getActivityList(Long memberId, String category, String scope, Long cursor);
}
