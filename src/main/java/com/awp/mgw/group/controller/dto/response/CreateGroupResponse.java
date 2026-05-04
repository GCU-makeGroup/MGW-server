package com.awp.mgw.group.controller.dto.response;

public record CreateGroupResponse(Long groupId) {
    public static CreateGroupResponse from(Long groupId) {
        return new CreateGroupResponse(groupId);
    }
}
