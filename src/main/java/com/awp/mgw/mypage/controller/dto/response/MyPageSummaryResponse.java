package com.awp.mgw.mypage.controller.dto.response;

public record MyPageSummaryResponse(
      long postCount,
      long groupCount,
      int point
) {
}