package com.awp.mgw.group.usecase.query;

import com.awp.mgw.group.controller.dto.response.GetGroupListResponse;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GetGroupListUseCase {
    // 그룹 목록 조회 진입점: 카테고리 필터가 없으면 전체 조회로 동작
    GetGroupListResponse getGroupList(Long memberId, List<Long> categoryIds, Pageable pageable);
}
