package com.awp.mgw.schedule.usecase.query;

import com.awp.mgw.schedule.controller.dto.response.ScheduleDetailResponse;

import java.time.LocalDate;
import java.util.List;

public interface GetDailyScheduleUseCase {
  List<ScheduleDetailResponse> getDailySchedules(Long memberId, LocalDate date);
}
