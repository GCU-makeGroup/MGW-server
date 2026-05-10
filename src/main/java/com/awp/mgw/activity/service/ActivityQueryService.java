package com.awp.mgw.activity.service;

import com.awp.mgw.activity.controller.dto.response.ActivityDetailResponse;
import com.awp.mgw.activity.controller.dto.response.ActivityListResponse;
import com.awp.mgw.activity.controller.dto.response.ActivitySummaryResponse;
import com.awp.mgw.activity.domain.Activity;
import com.awp.mgw.activity.domain.exception.ActivityDomainException;
import com.awp.mgw.activity.domain.exception.ActivityErrorCode;
import com.awp.mgw.activity.port.ActivityQueryRepository;
import com.awp.mgw.activity.port.ActivityRepository;
import com.awp.mgw.activity.usecase.GetActivityDetailUseCase;
import com.awp.mgw.activity.usecase.GetActivityListUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityQueryService implements GetActivityListUseCase, GetActivityDetailUseCase {

    private static final int PAGE_SIZE = 20;

    private final ActivityRepository activityRepository;
    private final ActivityQueryRepository activityQueryRepository;

    @Override
    public ActivityListResponse getActivityList(Long memberId, String category, String scope, Long cursor) {
        List<ActivityQueryRepository.ActivitySummaryRow> rows =
            activityQueryRepository.findActivitySummaries(memberId, category, scope, cursor, PAGE_SIZE + 1);

        boolean hasNext = rows.size() > PAGE_SIZE;
        List<ActivityQueryRepository.ActivitySummaryRow> pagedRows = hasNext ? rows.subList(0, PAGE_SIZE) : rows;

        ActivitySummaryResponse hotpick = null;
        if (scope == null || scope.isBlank()) {
            ActivityQueryRepository.ActivitySummaryRow hotpickRow = activityQueryRepository.findTopHotpick(memberId, category);
            hotpick = mapSummary(hotpickRow, hotpickRow != null ? hotpickRow.id() : null);
        }

        Long hotpickId = hotpick == null ? null : hotpick.id();
        List<ActivitySummaryResponse> activities = pagedRows.stream()
            .map(row -> mapSummary(row, hotpickId))
            .toList();

        String nextCursor = hasNext ? String.valueOf(pagedRows.get(pagedRows.size() - 1).id()) : null;
        return new ActivityListResponse(hotpick, activities, nextCursor);
    }

    @Override
    public ActivityDetailResponse getActivityDetail(Long memberId, Long activityId) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new ActivityDomainException(ActivityErrorCode.ACTIVITY_NOT_FOUND));

        ActivityQueryRepository.ActivitySummaryRow row = activityQueryRepository.findActivitySummaryById(activityId, memberId);
        if (row == null) {
            throw new ActivityDomainException(ActivityErrorCode.ACTIVITY_NOT_FOUND);
        }

        ActivityQueryRepository.ActivitySummaryRow hotpickRow = activityQueryRepository.findTopHotpick(memberId, null);
        Long hotpickId = hotpickRow == null ? null : hotpickRow.id();
        boolean isHotpick = hotpickId != null && hotpickId.equals(activityId);

        List<ActivityDetailResponse.ActivityMemberResponse> members = activityQueryRepository.findParticipants(activityId).stream()
            .map(participant -> new ActivityDetailResponse.ActivityMemberResponse(
                participant.userId(),
                participant.name(),
                participant.profileImg()
            ))
            .toList();

        return new ActivityDetailResponse(
            row.id(),
            row.title(),
            row.category(),
            row.capacity(),
            row.currentParticipants(),
            row.isLiked(),
            row.likeCount(),
            row.schedule(),
            row.thumbnail(),
            isHotpick,
            activity.getDescription(),
            members,
            activity.getOpenchatUrl()
        );
    }

    private ActivitySummaryResponse mapSummary(ActivityQueryRepository.ActivitySummaryRow row, Long hotpickId) {
        if (row == null) {
            return null;
        }

        boolean isHotpick = hotpickId != null && hotpickId.equals(row.id());
        return new ActivitySummaryResponse(
            row.id(),
            row.title(),
            row.category(),
            row.capacity(),
            row.currentParticipants(),
            row.isLiked(),
            row.likeCount(),
            row.schedule(),
            row.thumbnail(),
            isHotpick
        );
    }
}
