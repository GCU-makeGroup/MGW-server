package com.awp.mgw.activity.service;

import com.awp.mgw.activity.controller.dto.request.CreateActivityRequest;
import com.awp.mgw.activity.controller.dto.request.JoinActivityRequest;
import com.awp.mgw.activity.controller.dto.request.UpdateActivityRequest;
import com.awp.mgw.activity.controller.dto.response.ActivityIdResponse;
import com.awp.mgw.activity.domain.Activity;
import com.awp.mgw.activity.domain.ActivityCategory;
import com.awp.mgw.activity.domain.ActivityGroup;
import com.awp.mgw.activity.domain.enums.ActivityGroupStatus;
import com.awp.mgw.activity.domain.exception.ActivityDomainException;
import com.awp.mgw.activity.domain.exception.ActivityErrorCode;
import com.awp.mgw.activity.port.ActivityCategoryRepository;
import com.awp.mgw.activity.port.ActivityGroupRepository;
import com.awp.mgw.activity.port.ActivityQueryRepository;
import com.awp.mgw.activity.port.ActivityRepository;
import com.awp.mgw.activity.usecase.CreateActivityUseCase;
import com.awp.mgw.activity.usecase.DeleteActivityUseCase;
import com.awp.mgw.activity.usecase.JoinActivityUseCase;
import com.awp.mgw.activity.usecase.UpdateActivityUseCase;
import com.awp.mgw.category.domain.Category;
import com.awp.mgw.category.domain.exception.CategoryDomainException;
import com.awp.mgw.category.domain.exception.CategoryErrorCode;
import com.awp.mgw.category.port.CategoryRepository;
import com.awp.mgw.group.domain.Group;
import com.awp.mgw.group.domain.GroupMember;
import com.awp.mgw.group.port.GroupMemberRepository;
import com.awp.mgw.group.port.GroupRepository;
import com.awp.mgw.member.domain.Member;
import com.awp.mgw.member.domain.exception.MemberDomainException;
import com.awp.mgw.member.domain.exception.MemberErrorCode;
import com.awp.mgw.member.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityCommandService implements CreateActivityUseCase, UpdateActivityUseCase, DeleteActivityUseCase, JoinActivityUseCase {

    private static final List<ActivityGroupStatus> ACTIVE_JOIN_STATUSES =
        Arrays.asList(ActivityGroupStatus.PENDING, ActivityGroupStatus.JOIN);
    private static final String PERSONAL_GROUP_NAME_PREFIX = "personal-member-";
    private static final String PERSONAL_GROUP_TITLE_SUFFIX = "님의 1인 활동 그룹";
    private static final String PERSONAL_GROUP_CONTENT = "자동 생성된 개인 참여 그룹";

    private final ActivityRepository activityRepository;
    private final ActivityCategoryRepository activityCategoryRepository;
    private final ActivityGroupRepository activityGroupRepository;
    private final ActivityQueryRepository activityQueryRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    public ActivityIdResponse createActivity(Long memberId, CreateActivityRequest request) {
        Member member = getMemberOrThrow(memberId);

        Activity activity = Activity.create(
            request.title(),
            member,
            request.description(),
            request.maxMembers(),
            request.thumbnailUrl(),
            request.location(),
            request.schedule().toInstant(),
            request.openchatUrl()
        );

        Activity savedActivity = activityRepository.save(activity);
        saveActivityCategories(savedActivity, request.categoryIds());

        return ActivityIdResponse.from(savedActivity.getId());
    }

    @Override
    public ActivityIdResponse updateActivity(Long memberId, Long activityId, UpdateActivityRequest request) {
        Activity activity = getOwnedActivityOrThrow(activityId, memberId);

        activity.update(
            request.title(),
            request.description(),
            request.maxMembers(),
            request.thumbnailUrl(),
            request.location(),
            request.schedule().toInstant(),
            request.openchatUrl()
        );

        replaceActivityCategories(activity, request.categoryIds());
        return ActivityIdResponse.from(activity.getId());
    }

    @Override
    public ActivityIdResponse deleteActivity(Long memberId, Long activityId) {
        Activity activity = getOwnedActivityOrThrow(activityId, memberId);
        activityRepository.delete(activity);
        return ActivityIdResponse.from(activity.getId());
    }

    @Override
    public ActivityIdResponse joinActivity(Long memberId, Long activityId, JoinActivityRequest request) {
        Member member = getMemberOrThrow(memberId);
        Activity activity = getActivityOrThrow(activityId);

        validateCapacity(activity);
        if (activityQueryRepository.existsJoinedMember(activityId, memberId)) {
            throw new ActivityDomainException(ActivityErrorCode.DUPLICATE_ACTIVITY_MEMBER_JOIN);
        }

        Group targetGroup = resolveJoinGroup(member, request);
        validateGroupJoinDuplication(activity, targetGroup);

        activityGroupRepository.save(ActivityGroup.create(activity, targetGroup, ActivityGroupStatus.JOIN));
        return ActivityIdResponse.from(activity.getId());
    }

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberDomainException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private Activity getOwnedActivityOrThrow(Long activityId, Long memberId) {
        Activity activity = getActivityOrThrow(activityId);
        if (activity.getCreator() == null || !activity.getCreator().getId().equals(memberId)) {
            throw new ActivityDomainException(ActivityErrorCode.FORBIDDEN_ACTIVITY_ACCESS);
        }

        return activity;
    }

    private Activity getActivityOrThrow(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new ActivityDomainException(ActivityErrorCode.ACTIVITY_NOT_FOUND));
        return activity;
    }

    private void saveActivityCategories(Activity activity, List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }

        List<Category> categories = getCategoriesOrThrow(categoryIds);
        categories.forEach(category ->
            activityCategoryRepository.save(ActivityCategory.create(activity, category))
        );
    }

    private void replaceActivityCategories(Activity activity, List<Long> categoryIds) {
        if (categoryIds == null) {
            return;
        }

        activityCategoryRepository.deleteAllByActivity(activity);
        if (categoryIds.isEmpty()) {
            return;
        }

        List<Category> categories = getCategoriesOrThrow(categoryIds);
        categories.forEach(category ->
            activityCategoryRepository.save(ActivityCategory.create(activity, category))
        );
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

    private void validateCapacity(Activity activity) {
        long currentParticipants = activityQueryRepository.countJoinedParticipants(activity.getId());
        if (currentParticipants >= activity.getMaxMember()) {
            throw new ActivityDomainException(ActivityErrorCode.ACTIVITY_CAPACITY_EXCEEDED);
        }
    }

    private Group resolveJoinGroup(Member member, JoinActivityRequest request) {
        if (request.participationType() == JoinActivityRequest.ParticipationType.GROUP) {
            Long groupId = request.groupId();
            if (groupId == null) {
                throw new ActivityDomainException(ActivityErrorCode.ACTIVITY_GROUP_NOT_FOUND);
            }

            Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ActivityDomainException(ActivityErrorCode.ACTIVITY_GROUP_NOT_FOUND));

            if (!activityQueryRepository.existsMemberInGroup(groupId, member.getId())) {
                throw new ActivityDomainException(ActivityErrorCode.FORBIDDEN_ACTIVITY_ACCESS);
            }
            return group;
        }

        Group singleMemberGroup = activityQueryRepository.findSingleMemberGroup(member.getId());
        if (singleMemberGroup != null) {
            return singleMemberGroup;
        }

        Group createdGroup = groupRepository.save(Group.create(
            PERSONAL_GROUP_NAME_PREFIX + member.getId(),
            member.getName() + PERSONAL_GROUP_TITLE_SUFFIX,
            PERSONAL_GROUP_CONTENT,
            member,
            null,
            false,
            1
        ));
        groupMemberRepository.save(GroupMember.create(member, createdGroup));
        return createdGroup;
    }

    private void validateGroupJoinDuplication(Activity activity, Group group) {
        if (activityGroupRepository.existsByActivityAndGroupAndStatusIn(activity, group, ACTIVE_JOIN_STATUSES)) {
            throw new ActivityDomainException(ActivityErrorCode.DUPLICATE_ACTIVITY_GROUP_JOIN);
        }
    }
}
