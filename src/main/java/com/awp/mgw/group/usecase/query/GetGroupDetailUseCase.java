package com.awp.mgw.group.usecase.query;

import com.awp.mgw.group.controller.dto.response.GetGroupDetailResponse;

public interface GetGroupDetailUseCase {
    GetGroupDetailResponse getGroupDetail(Long memberId, Long groupId);
}
