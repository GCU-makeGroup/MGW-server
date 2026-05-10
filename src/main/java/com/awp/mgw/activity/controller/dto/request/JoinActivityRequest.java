package com.awp.mgw.activity.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;

public record JoinActivityRequest(
    @NotNull(message = "참여 유형은 필수입니다.")
    ParticipationType participationType,
    Long groupId
) {
    @AssertTrue(message = "GROUP 참여 시 groupId는 필수입니다.")
    public boolean isGroupIdValidForParticipationType() {
        if (participationType == ParticipationType.GROUP) {
            return groupId != null;
        }
        return true;
    }

    public enum ParticipationType {
        INDIVIDUAL,
        GROUP
    }
}
