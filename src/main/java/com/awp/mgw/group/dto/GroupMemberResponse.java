package com.awp.mgw.group.dto;

import com.awp.mgw.group.port.GroupMemberProjection;

public record GroupMemberResponse(
    Long groupMemberId,
    Long memberId,
    Long email,
    String name,
    String imageUrl,
    String introduction,
    Integer point
) {

    public static GroupMemberResponse from(GroupMemberProjection projection) {
        return new GroupMemberResponse(
            projection.getGroupMemberId(),
            projection.getMemberId(),
            projection.getEmail(),
            projection.getName(),
            projection.getImageUrl(),
            projection.getIntroduction(),
            projection.getPoint()
        );
    }
}
