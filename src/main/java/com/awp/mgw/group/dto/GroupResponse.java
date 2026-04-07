package com.awp.mgw.group.dto;

import com.awp.mgw.group.port.GroupSummaryProjection;

import java.time.LocalDateTime;

public record GroupResponse(
    Long id,
    String title,
    String content,
    Boolean isPublic,
    Long memberCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static GroupResponse from(GroupSummaryProjection projection) {
        return new GroupResponse(
            projection.getId(),
            projection.getTitle(),
            projection.getContent(),
            projection.getPublicGroup(),
            projection.getMemberCount(),
            projection.getCreatedAt(),
            projection.getUpdatedAt()
        );
    }
}
