package com.awp.mgw.mypage.controller.dto.response;

import com.awp.mgw.schedule.controller.dto.response.ScheduleDateResponse;

import java.time.LocalDate;
import java.util.List;

public record MyPageCalendarResponse(
      int year,
      int month,
      LocalDate selectedDate,
      List<ScheduleDateResponse> schedules
) {
}