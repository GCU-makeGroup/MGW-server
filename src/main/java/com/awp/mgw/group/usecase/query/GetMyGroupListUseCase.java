package com.awp.mgw.group.usecase.query;

import com.awp.mgw.group.controller.dto.response.GetGroupListResponse;
import org.springframework.data.domain.Pageable;

public interface GetMyGroupListUseCase {
    GetGroupListResponse getMyGroupList(Long memberId, Pageable pageable);
}
