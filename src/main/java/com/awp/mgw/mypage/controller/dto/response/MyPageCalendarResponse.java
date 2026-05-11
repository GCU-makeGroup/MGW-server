package com.awp.mgw.mypage.controller.dto.response;

import java.time.LocalDate;
import java.util.List;

public record MyPageCalendarResponse(
      int year,
      int month,
      LocalDate selectedDate,
      List<MyPageScheduleResponse> schedules
) {
}