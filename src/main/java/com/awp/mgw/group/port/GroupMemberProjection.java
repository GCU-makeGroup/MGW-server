package com.awp.mgw.group.port;

public interface GroupMemberProjection {

    Long getGroupMemberId();

    Long getMemberId();

    Long getEmail();

    String getName();

    String getImageUrl();

    String getIntroduction();

    Integer getPoint();
}
