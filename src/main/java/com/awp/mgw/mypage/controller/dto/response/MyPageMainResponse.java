package com.awp.mgw.mypage.controller.dto.response;

public record MyPageMainResponse(
      MyPageProfileResponse profile,
      MyPageSummaryResponse summary,
      MyPageCalendarResponse calendar
) {
}