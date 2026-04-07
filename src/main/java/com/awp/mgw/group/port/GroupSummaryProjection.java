package com.awp.mgw.group.port;

import java.time.LocalDateTime;

public interface GroupSummaryProjection {

    Long getId();

    String getTitle();

    String getContent();

    Boolean getPublicGroup();

    Long getMemberCount();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();
}
