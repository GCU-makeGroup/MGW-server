package com.awp.mgw.schedule.usecase.query;

import com.awp.mgw.schedule.controller.dto.response.ScheduleDateResponse;

import java.time.YearMonth;
import java.util.List;

public interface GetMonthlyScheduleUseCase {
  List<ScheduleDateResponse> getMonthlySchedules(Long memberId, YearMonth yearMonth);
}