package com.awp.mgw.activity.controller.dto.response;

import java.time.Instant;
import java.util.List;

public record ActivityDetailResponse(
    Long id,
    String title,
    String category,
    Integer capacity,
    Long currentParticipants,
    Boolean isLiked,
    Long likeCount,
    Instant schedule,
    String thumbnail,
    Boolean isHotpick,
    String description,
    List<ActivityMemberResponse> members,
    String openChatUrl,
    boolean isCreator,
    boolean isJoined
) {
    public record ActivityMemberResponse(
        Long userId,
        String name,
        String profileImg
    ) {
    }
}
