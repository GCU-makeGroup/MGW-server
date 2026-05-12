package com.awp.mgw.schedule.controller;

import com.awp.mgw.schedule.controller.dto.response.ScheduleDateResponse;
import com.awp.mgw.schedule.usecase.query.GetMonthlyScheduleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import java.time.YearMonth;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

  private final GetMonthlyScheduleUseCase getMonthlyScheduleUseCase;

  @GetMapping
  public List<ScheduleDateResponse> getMonthlySchedules(
        @AuthenticationPrincipal Long memberId,

        @Min(value = 2000, message = "연도는 2000년 이상이어야 합니다.")
        @RequestParam int year,

        @Min(value = 1, message = "월은 1 이상이어야 합니다.")
        @Max(value = 12, message = "월은 12 이하여야 합니다.")
        @RequestParam int month
  ) {
    return getMonthlyScheduleUseCase.getMonthlySchedules(
          memberId,
          YearMonth.of(year, month)
    );
  }
}