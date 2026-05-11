package com.awp.mgw.activity.usecase;

import com.awp.mgw.activity.controller.dto.response.ActivityIdResponse;

public interface LeaveActivityUseCase {
    ActivityIdResponse leaveActivity(Long memberId, Long activityId);
}
