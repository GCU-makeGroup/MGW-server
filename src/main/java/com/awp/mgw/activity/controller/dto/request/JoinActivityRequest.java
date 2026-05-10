package com.awp.mgw.activity.controller.dto.request;

import jakarta.validation.constraints.NotNull;

public record JoinActivityRequest(
    @NotNull(message = "참여 유형은 필수입니다.")
    ParticipationType participationType,
    Long groupId
) {
    public enum ParticipationType {
        INDIVIDUAL,
        GROUP
    }
}
