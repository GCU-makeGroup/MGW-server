package com.awp.mgw.activity.service;

import com.awp.mgw.activity.controller.dto.request.CreateActivityRequest;
import com.awp.mgw.activity.controller.dto.request.JoinActivityRequest;
import com.awp.mgw.activity.controller.dto.request.UpdateActivityRequest;
import com.awp.mgw.activity.controller.dto.response.ActivityIdResponse;
import com.awp.mgw.activity.controller.dto.response.ActivityImageUploadResponse;
import com.awp.mgw.activity.domain.Activity;
import com.awp.mgw.activity.domain.ActivityCategory;
import com.awp.mgw.activity.domain.ActivityGroup;
import com.awp.mgw.activity.domain.ActivityLike;
import com.awp.mgw.activity.domain.enums.ActivityGroupStatus;
import com.awp.mgw.activity.domain.exception.ActivityDomainException;
import com.awp.mgw.activity.domain.exception.ActivityErrorCode;
import com.awp.mgw.activity.port.ActivityCategoryRepository;
import com.awp.mgw.activity.port.ActivityGroupRepository;
import com.awp.mgw.activity.port.ActivityLikeRepository;
import com.awp.mgw.activity.port.ActivityQueryRepository;
import com.awp.mgw.activity.port.ActivityRepository;
import com.awp.mgw.activity.usecase.CreateActivityUseCase;
import com.awp.mgw.activity.usecase.DeleteActivityUseCase;
import com.awp.mgw.activity.usecase.JoinActivityUseCase;
import com.awp.mgw.activity.usecase.LeaveActivityUseCase;
import com.awp.mgw.activity.usecase.LikeActivityUseCase;
import com.awp.mgw.activity.usecase.UnlikeActivityUseCase;
import com.awp.mgw.activity.usecase.UploadActivityImageUseCase;
import com.awp.mgw.activity.usecase.UpdateActivityUseCase;
import com.awp.mgw.category.domain.Category;
import com.awp.mgw.category.domain.exception.CategoryDomainException;
import com.awp.mgw.category.domain.exception.CategoryErrorCode;
import com.awp.mgw.category.port.CategoryRepository;
import com.awp.mgw.global.util.FileUtil;
import com.awp.mgw.group.domain.Group;
import com.awp.mgw.group.domain.GroupMember;
import com.awp.mgw.group.port.GroupMemberRepository;
import com.awp.mgw.group.port.GroupRepository;
import com.awp.mgw.member.domain.Member;
import com.awp.mgw.member.domain.exception.MemberDomainException;
import com.awp.mgw.member.domain.exception.MemberErrorCode;
import com.awp.mgw.member.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityCommandService implements
    CreateActivityUseCase,
    UpdateActivityUseCase,
    DeleteActivityUseCase,
    JoinActivityUseCase,
    LeaveActivityUseCase,
    LikeActivityUseCase,
    UnlikeActivityUseCase,
    UploadActivityImageUseCase {

    private static final List<ActivityGroupStatus> ACTIVE_JOIN_STATUSES =
          Arrays.asList(ActivityGroupStatus.PENDING, ActivityGroupStatus.JOIN);
    private static final String PERSONAL_GROUP_NAME_PREFIX = "personal-member-";
    private static final String PERSONAL_GROUP_TITLE_SUFFIX = "님의 1인 활동 그룹";
    private static final String PERSONAL_GROUP_CONTENT = "자동 생성된 개인 참여 그룹";

    private final ActivityRepository activityRepository;
    private final ActivityCategoryRepository activityCategoryRepository;
    private final ActivityGroupRepository activityGroupRepository;
    private final ActivityLikeRepository activityLikeRepository;
    private final ActivityQueryRepository activityQueryRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final FileUtil fileUtil;
    @Value("${file.upload.public-base-url:http://localhost:8080/mgw/uploads}")
    private String uploadPublicBaseUrl;

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
        Group targetGroup = resolveJoinGroup(member, request);
        Activity activity = getActivityForUpdateOrThrow(activityId);

        validateGroupJoinDuplication(activity, targetGroup);
        validateMemberJoinDuplication(activityId, memberId, targetGroup.getId());
        validateCapacity(activity, targetGroup.getId());

        try {
            activityGroupRepository.save(ActivityGroup.create(activity, targetGroup, ActivityGroupStatus.JOIN));
        } catch (DataIntegrityViolationException e) {
            throw new ActivityDomainException(ActivityErrorCode.DUPLICATE_ACTIVITY_GROUP_JOIN);
        }
        return ActivityIdResponse.from(activity.getId());
    }

    @Override
    public ActivityIdResponse leaveActivity(Long memberId, Long activityId) {
        Activity activity = getActivityForUpdateOrThrow(activityId);
        validateHostCanLeave(activity, memberId);

        int deletedCount = activityGroupRepository.deleteByActivityIdAndMemberIdAndStatusIn(
              activityId,
              memberId,
              ACTIVE_JOIN_STATUSES
        );

        if (deletedCount == 0) {
            throw new ActivityDomainException(ActivityErrorCode.ACTIVITY_MEMBER_NOT_FOUND);
        }

        return ActivityIdResponse.from(activityId);
    }

    @Override
    public ActivityIdResponse likeActivity(Long memberId, Long activityId) {
        Member member = getMemberOrThrow(memberId);
        Activity activity = getActivityOrThrow(activityId);

        if (activityLikeRepository.existsByMemberAndActivity(member, activity)) {
            throw new ActivityDomainException(ActivityErrorCode.DUPLICATE_ACTIVITY_LIKE);
        }

        try {
            activityLikeRepository.save(ActivityLike.create(member, activity));
        } catch (DataIntegrityViolationException e) {
            throw new ActivityDomainException(ActivityErrorCode.DUPLICATE_ACTIVITY_LIKE);
        }
        return ActivityIdResponse.from(activityId);
    }

    @Override
    public ActivityIdResponse unlikeActivity(Long memberId, Long activityId) {
        Member member = getMemberOrThrow(memberId);
        Activity activity = getActivityOrThrow(activityId);

        ActivityLike activityLike = activityLikeRepository.findByMemberAndActivity(member, activity)
              .orElseThrow(() -> new ActivityDomainException(ActivityErrorCode.ACTIVITY_LIKE_NOT_FOUND));

        activityLikeRepository.delete(activityLike);
        return ActivityIdResponse.from(activityId);
    }

    @Override
    public ActivityImageUploadResponse uploadActivityImage(Long memberId, MultipartFile file) {
        getMemberOrThrow(memberId);
        validateImageFile(file);
        String thumbnailPath = fileUtil.saveFile(file, "activities");
        return ActivityImageUploadResponse.from(buildPublicUrl(thumbnailPath));
    }

    private Member getMemberOrThrow (Long memberId){
        return memberRepository.findById(memberId)
              .orElseThrow(() -> new MemberDomainException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private Activity getOwnedActivityOrThrow (Long activityId, Long memberId){
        Activity activity = getActivityOrThrow(activityId);
        if (activity.getCreator() == null || !activity.getCreator().getId().equals(memberId)) {
            throw new ActivityDomainException(ActivityErrorCode.FORBIDDEN_ACTIVITY_ACCESS);
        }
        return activity;
    }

    private Activity getActivityOrThrow (Long activityId){
        Activity activity = activityRepository.findById(activityId)
              .orElseThrow(() -> new ActivityDomainException(ActivityErrorCode.ACTIVITY_NOT_FOUND));
        return activity;
    }

    private Activity getActivityForUpdateOrThrow (Long activityId){
        return activityRepository.findByIdForUpdate(activityId)
              .orElseThrow(() -> new ActivityDomainException(ActivityErrorCode.ACTIVITY_NOT_FOUND));
    }

    private void saveActivityCategories (Activity activity, List < Long > categoryIds){
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }

        List<Category> categories = getCategoriesOrThrow(categoryIds);
        categories.forEach(category ->
              activityCategoryRepository.save(ActivityCategory.create(activity, category))
        );
    }

    private void replaceActivityCategories (Activity activity, List < Long > categoryIds){
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

    private List<Category> getCategoriesOrThrow (List < Long > categoryIds) {
        List<Long> distinctCategoryIds = categoryIds.stream()
              .distinct()
              .toList();
        List<Category> categories = categoryRepository.findAllById(distinctCategoryIds);

        if (categories.size() != distinctCategoryIds.size()) {
            throw new CategoryDomainException(CategoryErrorCode.CATEGORY_NOT_FOUND);
        }

        return categories;
    }

    private void validateCapacity (Activity activity, Long groupId){
        long currentParticipants = activityQueryRepository.countJoinedParticipants(activity.getId());
        long groupMembers = activityQueryRepository.countGroupMembers(groupId);
        long overlapMembers = activityQueryRepository.countAlreadyJoinedMembersFromGroup(activity.getId(), groupId);
        long expectedParticipants = currentParticipants + Math.max(0L, groupMembers - overlapMembers);

        if (expectedParticipants > activity.getMaxMember()) {
            throw new ActivityDomainException(ActivityErrorCode.ACTIVITY_CAPACITY_EXCEEDED);
        }
    }

    private Group resolveJoinGroup (Member member, JoinActivityRequest request){
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

    private void validateGroupJoinDuplication (Activity activity, Group group){
        if (activityGroupRepository.existsByActivityAndGroupAndStatusIn(activity, group, ACTIVE_JOIN_STATUSES)) {
            throw new ActivityDomainException(ActivityErrorCode.DUPLICATE_ACTIVITY_GROUP_JOIN);
        }
    }

    private void validateMemberJoinDuplication (Long activityId, Long memberId, Long groupId){
        if (activityQueryRepository.existsMemberInActivityByStatuses(activityId, memberId, ACTIVE_JOIN_STATUSES)) {
            throw new ActivityDomainException(ActivityErrorCode.DUPLICATE_ACTIVITY_MEMBER_JOIN);
        }

        long overlapMembers = activityQueryRepository.countAlreadyJoinedMembersFromGroup(activityId, groupId);
        if (overlapMembers > 0) {
            throw new ActivityDomainException(ActivityErrorCode.DUPLICATE_ACTIVITY_MEMBER_JOIN);
        }
    }

    private void validateHostCanLeave (Activity activity, Long memberId){
        if (activity.getCreator() != null && activity.getCreator().getId().equals(memberId)) {
            throw new ActivityDomainException(ActivityErrorCode.HOST_CANNOT_LEAVE_ACTIVITY);
        }
    }

    private void validateImageFile (MultipartFile file){
        if (file == null || file.isEmpty()) {
            throw new ActivityDomainException(ActivityErrorCode.INVALID_ACTIVITY_THUMBNAIL_URL);
        }
    }

    private String buildPublicUrl (String imagePath){
        String normalizedBase = uploadPublicBaseUrl.endsWith("/")
              ? uploadPublicBaseUrl.substring(0, uploadPublicBaseUrl.length() - 1)
              : uploadPublicBaseUrl;
        return normalizedBase + "/" + imagePath;
    }
}
