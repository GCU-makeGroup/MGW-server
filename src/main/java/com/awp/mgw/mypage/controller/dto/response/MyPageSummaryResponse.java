package com.awp.mgw.mypage.controller.dto.response;

public record MyPageSummaryResponse(
      long activityCount,
      long groupCount,
      int point
) {
}