package com.awp.mgw.activity.service;

import com.awp.mgw.activity.controller.dto.request.CreateActivityRequest;
import com.awp.mgw.activity.controller.dto.request.UpdateActivityRequest;
import com.awp.mgw.activity.controller.dto.response.ActivityIdResponse;
import com.awp.mgw.activity.domain.Activity;
import com.awp.mgw.activity.domain.ActivityCategory;
import com.awp.mgw.activity.domain.exception.ActivityDomainException;
import com.awp.mgw.activity.domain.exception.ActivityErrorCode;
import com.awp.mgw.activity.port.ActivityCategoryRepository;
import com.awp.mgw.activity.port.ActivityRepository;
import com.awp.mgw.activity.usecase.CreateActivityUseCase;
import com.awp.mgw.activity.usecase.DeleteActivityUseCase;
import com.awp.mgw.activity.usecase.UpdateActivityUseCase;
import com.awp.mgw.category.domain.Category;
import com.awp.mgw.category.domain.exception.CategoryDomainException;
import com.awp.mgw.category.domain.exception.CategoryErrorCode;
import com.awp.mgw.category.port.CategoryRepository;
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
public class ActivityCommandService implements CreateActivityUseCase, UpdateActivityUseCase, DeleteActivityUseCase {

    private final ActivityRepository activityRepository;
    private final ActivityCategoryRepository activityCategoryRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

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

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberDomainException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private Activity getOwnedActivityOrThrow(Long activityId, Long memberId) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new ActivityDomainException(ActivityErrorCode.ACTIVITY_NOT_FOUND));

        if (activity.getCreator() == null || !activity.getCreator().getId().equals(memberId)) {
            throw new ActivityDomainException(ActivityErrorCode.FORBIDDEN_ACTIVITY_ACCESS);
        }

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
}
