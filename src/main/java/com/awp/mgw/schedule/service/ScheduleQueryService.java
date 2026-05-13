package com.awp.mgw.schedule.service;

import com.awp.mgw.activity.port.ActivityQueryRepository;
import com.awp.mgw.schedule.controller.dto.response.ScheduleDateResponse;
import com.awp.mgw.schedule.usecase.query.GetMonthlyScheduleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    ZoneId zoneId = ZoneId.systemDefault();

    LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
    LocalDateTime endDateTime = yearMonth
          .plusMonths(1)
          .atDay(1)
          .atStartOfDay();

    List<LocalDate> scheduleDates =
          activityQueryRepository.findMonthlyScheduleDates(
                memberId,
                startDateTime.atZone(zoneId).toInstant(),
                endDateTime.atZone(zoneId).toInstant()
          );

    return scheduleDates.stream()
          .map(date -> new ScheduleDateResponse(date, true))
          .toList();
  }
}