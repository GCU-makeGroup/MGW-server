package com.awp.mgw.schedule.controller;

import com.awp.mgw.schedule.controller.dto.response.ScheduleDateResponse;
import com.awp.mgw.schedule.usecase.query.GetMonthlyScheduleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

  private final GetMonthlyScheduleUseCase getMonthlyScheduleUseCase;

  @GetMapping
  public List<ScheduleDateResponse> getMonthlySchedules(
        @AuthenticationPrincipal Long memberId,
        @RequestParam int year,
        @RequestParam int month
  ) {
    return getMonthlyScheduleUseCase.getMonthlySchedules(
          memberId,
          YearMonth.of(year, month)
    );
  }
}