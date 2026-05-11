package com.awp.mgw.schedule.service;

import com.awp.mgw.activity.port.ActivityQueryRepository;
import com.awp.mgw.schedule.controller.dto.response.ScheduleDateResponse;
import com.awp.mgw.schedule.usecase.query.GetMonthlyScheduleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleQueryService implements GetMonthlyScheduleUseCase {

  private final ActivityQueryRepository activityQueryRepository;

  @Override
  public List<ScheduleDateResponse> getMonthlySchedules(Long memberId, YearMonth yearMonth) {

    LocalDate firstDay = yearMonth.atDay(1);
    LocalDate lastDay = yearMonth.atEndOfMonth();

    List<LocalDate> scheduleDates =
          activityQueryRepository.findMonthlyScheduleDates(
                memberId,
                firstDay.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                lastDay.atTime(23, 59, 59)
                      .atZone(ZoneId.systemDefault())
                      .toInstant()
          );

    return scheduleDates.stream()
          .map(date -> new ScheduleDateResponse(date, true))
          .toList();
  }
}