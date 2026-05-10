package com.awp.mgw.activity.controller.dto.response;

import java.time.Instant;

public record ActivitySummaryResponse(
    Long id,
    String title,
    String category,
    Integer capacity,
    Long currentParticipants,
    Boolean isLiked,
    Long likeCount,
    Instant schedule,
    String thumbnail,
    Boolean isHotpick
) {
}
