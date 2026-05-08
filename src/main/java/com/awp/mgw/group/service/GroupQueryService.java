package com.awp.mgw.group.service;

import com.awp.mgw.category.domain.Category;
import com.awp.mgw.group.controller.dto.response.GetGroupListResponse;
import com.awp.mgw.group.domain.Group;
import com.awp.mgw.group.port.CommentRepository;
import com.awp.mgw.group.port.GroupQueryRepository;
import com.awp.mgw.group.usecase.query.GetGroupListUseCase;
import com.awp.mgw.member.domain.Member;
import com.awp.mgw.member.domain.exception.MemberDomainException;
import com.awp.mgw.member.domain.exception.MemberErrorCode;
import com.awp.mgw.member.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupQueryService implements GetGroupListUseCase {

    private final GroupQueryRepository groupQueryRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Override
    public GetGroupListResponse getGroupList(Long memberId, List<Long> categoryIds, Pageable pageable) {
        getMemberOrThrow(memberId);

        // 목록 대상 그룹만 먼저 가져온 뒤, 댓글 수와 카테고리는 groupId 기준 Map으로 한 번에 붙임
        Page<Group> groupPage = groupQueryRepository.findGroupList(categoryIds, pageable);
        Map<Long, Integer> commentCountByGroupId = getCommentCountByGroupId(groupPage.getContent());
        Map<Long, List<Category>> categoriesByGroupId = getCategoriesByGroupId(groupPage.getContent());

        return GetGroupListResponse.from(groupPage, commentCountByGroupId, categoriesByGroupId);
    }

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberDomainException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private Map<Long, Integer> getCommentCountByGroupId(List<Group> groups) {
        // 댓글 수를 그룹별로 집계해서 DTO 조립 시 추가 쿼리가 나가지 않게 함
        List<Long> groupIds = groups.stream()
                .map(Group::getId)
                .toList();

        if (groupIds.isEmpty()) {
            return Map.of();
        }

        return commentRepository.countCommentsByGroupIds(groupIds).stream()
                .collect(Collectors.toMap(
                        CommentRepository.CommentCountProjection::getGroupId,
                        projection -> projection.getCommentCount().intValue()
                ));
    }

    private Map<Long, List<Category>> getCategoriesByGroupId(List<Group> groups) {
        // 카테고리도 그룹별 Map으로 미리 조회해서 group.getGroupCategories() 접근으로 인한 N+1을 피함
        List<Long> groupIds = groups.stream()
                .map(Group::getId)
                .toList();

        if (groupIds.isEmpty()) {
            return Map.of();
        }

        return groupQueryRepository.findCategoriesByGroupIds(groupIds);
    }
}
