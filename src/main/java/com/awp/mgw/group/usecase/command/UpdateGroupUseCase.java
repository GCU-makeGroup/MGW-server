package com.awp.mgw.group.usecase.command;

import com.awp.mgw.group.controller.dto.request.CreateGroupRequest;
import com.awp.mgw.group.controller.dto.response.CreateGroupResponse;

public interface UpdateGroupUseCase {
    CreateGroupResponse updateGroup(Long memberId, Long groupId, CreateGroupRequest request);
}
