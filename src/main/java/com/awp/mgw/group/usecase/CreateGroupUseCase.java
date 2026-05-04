package com.awp.mgw.group.usecase;

import com.awp.mgw.group.controller.dto.request.CreateGroupRequest;
import com.awp.mgw.group.controller.dto.response.CreateGroupResponse;

public interface CreateGroupUseCase {
    CreateGroupResponse createGroup(Long memberId, CreateGroupRequest request);
}
