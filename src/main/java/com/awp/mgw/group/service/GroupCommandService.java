package com.awp.mgw.group.service;

import com.awp.mgw.category.domain.Category;
import com.awp.mgw.category.domain.exception.CategoryDomainException;
import com.awp.mgw.category.domain.exception.CategoryErrorCode;
import com.awp.mgw.category.port.CategoryRepository;
import com.awp.mgw.group.controller.dto.request.CreateGroupRequest;
import com.awp.mgw.group.controller.dto.response.CreateGroupResponse;
import com.awp.mgw.group.domain.Group;
import com.awp.mgw.group.domain.GroupCategory;
import com.awp.mgw.group.domain.GroupMember;
import com.awp.mgw.group.domain.exception.GroupDomainException;
import com.awp.mgw.group.domain.exception.GroupErrorCode;
import com.awp.mgw.group.port.GroupCategoryRepository;
import com.awp.mgw.group.port.GroupMemberRepository;
import com.awp.mgw.group.port.GroupRepository;
import com.awp.mgw.group.usecase.command.CreateGroupUseCase;
import com.awp.mgw.group.usecase.command.JoinGroupUseCase;
import com.awp.mgw.group.usecase.command.UpdateGroupUseCase;
import com.awp.mgw.member.domain.Member;
import com.awp.mgw.member.domain.exception.MemberDomainException;
import com.awp.mgw.member.domain.exception.MemberErrorCode;
import com.awp.mgw.member.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupCommandService implements CreateGroupUseCase, UpdateGroupUseCase, JoinGroupUseCase {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupCategoryRepository groupCategoryRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public CreateGroupResponse createGroup(Long memberId, CreateGroupRequest request) {
        // 1. 요청 데이터 검증
        validateCapacity(request.capacity());

        // 2. 연관된 엔티티 조회 및 검증
        Member member = getMemberOrThrow(memberId);
        List<Category> categories = getCategoriesOrThrow(request.categoryIds());

        // 3. 그룹 엔티티 생성 및 저장
        Group savedGroup = groupRepository.save(Group.create(
                request.name(),
                request.title(),
                request.content(),
                member,
                request.imageUrl(),
                request.isPublic(),
                request.capacity()
        ));

        // 4. 연관관계 데이터 생성 및 저장 (그룹장 설정 및 카테고리 매핑)
        groupMemberRepository.save(GroupMember.create(member, savedGroup));
        categories.forEach(category ->
            groupCategoryRepository.save(GroupCategory.create(category, savedGroup))
        );

        return CreateGroupResponse.from(savedGroup.getId());
    }

    @Override
    public CreateGroupResponse updateGroup(Long memberId, Long groupId, CreateGroupRequest request) {
        Member member = getMemberOrThrow(memberId);
        Group group = getOwnedGroupOrThrow(groupId, member.getId());
        List<Category> categories = getCategoriesOrThrow(request.categoryIds());

        group.updateGroup(
            request.name(),
            request.title(),
            request.content(),
            request.imageUrl(),
            request.isPublic(),
            request.capacity()
        );

        // 원래 존재하던 그룹 카테고리 삭제 후 수정한 그룹 카테고리 생성
        groupCategoryRepository.deleteAllByGroup(group);
        categories.forEach(category ->
            groupCategoryRepository.save(GroupCategory.create(category, group))
        );

        return CreateGroupResponse.from(group.getId());
    }

    @Override
    public void joinGroup(Long memberId, Long groupId) {
        Member member = getMemberOrThrow(memberId);
        Group group = groupRepository.findByIdForUpdate(groupId)
                .orElseThrow(() -> new GroupDomainException(GroupErrorCode.GROUP_NOT_FOUND));

        if (!group.getIsPublic()) {
            throw new GroupDomainException(GroupErrorCode.PRIVATE_GROUP_CANNOT_JOIN);
        }

        if (groupMemberRepository.existsByMember_IdAndGroup_Id(member.getId(), group.getId())) {
            throw new GroupDomainException(GroupErrorCode.MEMBER_ALREADY_JOINED_GROUP);
        }

        long currentMemberCount = groupMemberRepository.countByGroup_Id(group.getId());
        if (currentMemberCount >= group.getCapacity()) {
            throw new GroupDomainException(GroupErrorCode.GROUP_CAPACITY_EXCEEDED);
        }

        groupMemberRepository.save(GroupMember.create(member, group));
    }

    /**
     * 그룹 정원 유효성 검증
     */
    private void validateCapacity(Integer capacity) {
        if (capacity == null || capacity < 1 || capacity > 20) {
            throw new GroupDomainException(GroupErrorCode.INVALID_GROUP_CAPACITY);
        }
    }

    /**
     * 회원 엔티티 조회 및 예외 처리
     */
    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberDomainException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private Group getOwnedGroupOrThrow(Long groupId, Long memberId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new GroupDomainException(GroupErrorCode.GROUP_NOT_FOUND));

        if (group.getMember() == null || !group.getMember().getId().equals(memberId)) {
            throw new GroupDomainException(GroupErrorCode.GROUP_NOT_OWNED);
        }

        return group;
    }

    private List<Category> getCategoriesOrThrow(List<Long> categoryIds) {
        List<Long> distinctCategoryIds = categoryIds.stream()
            .distinct()
            .toList();
        List<Category> categories = categoryRepository.findAllById(distinctCategoryIds);

        if (categories.size() != distinctCategoryIds.size()) {
            throw new CategoryDomainException(CategoryErrorCode.CATEGORY_NOT_FOUND);
        }

        return categories;
    }
}
