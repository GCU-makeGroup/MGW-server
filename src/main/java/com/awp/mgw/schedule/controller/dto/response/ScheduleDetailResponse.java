package com.awp.mgw.schedule.controller.dto.response;

import java.time.Instant;

public record ScheduleDetailResponse(
    Long activityId,
    String title,
    String category,
    String location,
    Instant schedule,
    Integer capacity,
    Long currentParticipants
) {}
