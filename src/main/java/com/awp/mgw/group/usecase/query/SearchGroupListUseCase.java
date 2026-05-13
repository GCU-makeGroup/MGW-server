package com.awp.mgw.group.usecase.query;

import com.awp.mgw.group.controller.dto.response.GetGroupListResponse;
import org.springframework.data.domain.Pageable;

public interface SearchGroupListUseCase {
    GetGroupListResponse searchGroupList(Long memberId, String keyword, Pageable pageable);
}
