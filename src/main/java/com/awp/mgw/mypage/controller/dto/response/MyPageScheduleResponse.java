package com.awp.mgw.mypage.controller.dto.response;

import java.time.LocalDate;

public record MyPageScheduleResponse(
      LocalDate date,
      boolean hasSchedule
) {
}