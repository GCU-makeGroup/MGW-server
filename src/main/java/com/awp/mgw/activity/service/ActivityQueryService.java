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
import com.awp.mgw.activity.usecase.SearchActivityUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityQueryService implements GetActivityListUseCase, GetActivityDetailUseCase, SearchActivityUseCase {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final ActivityRepository activityRepository;
    private final ActivityQueryRepository activityQueryRepository;

    @Override
    public ActivityListResponse getActivityList(Long memberId, String category, String scope, Integer limit, Long cursor) {
        validateScopeMemberId(scope, memberId);
        int pageSize = normalizeLimit(limit);

        ActivityQueryRepository.ActivitySummaryRow hotpickRow = activityQueryRepository.findTopHotpick(memberId, null);
        Long hotpickId = hotpickRow == null ? null : hotpickRow.id();
        ActivitySummaryResponse hotpick = mapSummary(hotpickRow, hotpickId);

        if ("hotpick".equalsIgnoreCase(scope)) {
            List<ActivitySummaryResponse> onlyHotpick = hotpick == null ? List.of() : List.of(hotpick);
            return new ActivityListResponse(hotpick, onlyHotpick, null);
        }

        List<ActivityQueryRepository.ActivitySummaryRow> rows =
            activityQueryRepository.findActivitySummaries(memberId, category, scope, cursor, pageSize + 1);

        boolean hasNext = rows.size() > pageSize;
        List<ActivityQueryRepository.ActivitySummaryRow> pagedRows = hasNext ? rows.subList(0, pageSize) : rows;

        List<ActivitySummaryResponse> activities = pagedRows.stream()
            .map(row -> mapSummary(row, hotpickId))
            .toList();

        String nextCursor = hasNext ? String.valueOf(pagedRows.get(pagedRows.size() - 1).id()) : null;
        ActivitySummaryResponse responseHotpick = (scope == null || scope.isBlank()) ? hotpick : null;
        return new ActivityListResponse(responseHotpick, activities, nextCursor);
    }

    @Override
    public ActivityListResponse searchActivities(Long memberId, String keyword, Integer limit, Long cursor) {
        validateKeyword(keyword);
        int pageSize = normalizeLimit(limit);

        List<ActivityQueryRepository.ActivitySummaryRow> rows =
            activityQueryRepository.searchActivitySummaries(memberId, keyword, cursor, pageSize + 1);

        boolean hasNext = rows.size() > pageSize;
        List<ActivityQueryRepository.ActivitySummaryRow> pagedRows = hasNext ? rows.subList(0, pageSize) : rows;

        ActivityQueryRepository.ActivitySummaryRow hotpickRow = activityQueryRepository.findTopHotpick(memberId, null);
        Long hotpickId = hotpickRow == null ? null : hotpickRow.id();

        List<ActivitySummaryResponse> activities = pagedRows.stream()
            .map(row -> mapSummary(row, hotpickId))
            .toList();

        String nextCursor = hasNext ? String.valueOf(pagedRows.get(pagedRows.size() - 1).id()) : null;
        return new ActivityListResponse(null, activities, nextCursor);
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
        boolean canViewOpenChat = memberId != null && activityQueryRepository.existsJoinedMember(activityId, memberId);

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
            canViewOpenChat ? activity.getOpenchatUrl() : null
        );
    }

    private void validateScopeMemberId(String scope, Long memberId) {
        if (scope == null || scope.isBlank()) {
            return;
        }

        if (("joined".equalsIgnoreCase(scope) || "created".equalsIgnoreCase(scope)) && memberId == null) {
            throw new ActivityDomainException(ActivityErrorCode.MEMBER_ID_REQUIRED_FOR_SCOPE);
        }
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return limit;
    }

    private void validateKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new ActivityDomainException(ActivityErrorCode.INVALID_ACTIVITY_TITLE);
        }
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
