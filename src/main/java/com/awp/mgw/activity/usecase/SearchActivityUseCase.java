package com.awp.mgw.activity.usecase;

import com.awp.mgw.activity.controller.dto.response.ActivityListResponse;

public interface SearchActivityUseCase {
    ActivityListResponse searchActivities(Long memberId, String keyword, Integer limit, Long cursor);
}
