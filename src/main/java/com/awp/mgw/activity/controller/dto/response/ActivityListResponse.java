package com.awp.mgw.activity.controller.dto.response;

import java.util.List;

public record ActivityListResponse(
    ActivitySummaryResponse hotpick,
    List<ActivitySummaryResponse> activities,
    String nextCursor
) {
}
