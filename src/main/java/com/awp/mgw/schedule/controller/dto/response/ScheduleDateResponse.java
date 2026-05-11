package com.awp.mgw.schedule.controller.dto.response;

import java.time.LocalDate;

public record ScheduleDateResponse(
      LocalDate date,
      boolean hasSchedule
) {
}