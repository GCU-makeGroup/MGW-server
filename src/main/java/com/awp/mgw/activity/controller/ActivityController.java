package com.awp.mgw.activity.controller;

import com.awp.mgw.activity.controller.dto.request.CreateActivityRequest;
import com.awp.mgw.activity.controller.dto.request.JoinActivityRequest;
import com.awp.mgw.activity.controller.dto.request.UpdateActivityRequest;
import com.awp.mgw.activity.controller.dto.response.ActivityDetailResponse;
import com.awp.mgw.activity.controller.dto.response.ActivityIdResponse;
import com.awp.mgw.activity.controller.dto.response.ActivityImageUploadResponse;
import com.awp.mgw.activity.controller.dto.response.ActivityListResponse;
import com.awp.mgw.activity.usecase.CreateActivityUseCase;
import com.awp.mgw.activity.usecase.DeleteActivityUseCase;
import com.awp.mgw.activity.usecase.GetActivityDetailUseCase;
import com.awp.mgw.activity.usecase.GetActivityListUseCase;
import com.awp.mgw.activity.usecase.JoinActivityUseCase;
import com.awp.mgw.activity.usecase.LeaveActivityUseCase;
import com.awp.mgw.activity.usecase.LikeActivityUseCase;
import com.awp.mgw.activity.usecase.SearchActivityUseCase;
import com.awp.mgw.activity.usecase.UnlikeActivityUseCase;
import com.awp.mgw.activity.usecase.UploadActivityImageUseCase;
import com.awp.mgw.activity.usecase.UpdateActivityUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
@Tag(name = "Activity | 활동", description = "활동 관련 API")
public class ActivityController {

    private final CreateActivityUseCase createActivityUseCase;
    private final UpdateActivityUseCase updateActivityUseCase;
    private final DeleteActivityUseCase deleteActivityUseCase;
    private final GetActivityListUseCase getActivityListUseCase;
    private final GetActivityDetailUseCase getActivityDetailUseCase;
    private final JoinActivityUseCase joinActivityUseCase;
    private final LeaveActivityUseCase leaveActivityUseCase;
    private final LikeActivityUseCase likeActivityUseCase;
    private final UnlikeActivityUseCase unlikeActivityUseCase;
    private final SearchActivityUseCase searchActivityUseCase;
    private final UploadActivityImageUseCase uploadActivityImageUseCase;

    @PostMapping
    @Operation(summary = "활동 생성", description = "신규 활동을 생성합니다.")
    public ActivityIdResponse createActivity(
        @AuthenticationPrincipal Long memberId,
        @Valid @RequestBody CreateActivityRequest request
    ) {
        return createActivityUseCase.createActivity(memberId, request);
    }

    @PutMapping("/{activityId}")
    @Operation(summary = "활동 수정", description = "기존 활동을 수정합니다.")
    public ActivityIdResponse updateActivity(
        @AuthenticationPrincipal Long memberId,
        @PathVariable Long activityId,
        @Valid @RequestBody UpdateActivityRequest request
    ) {
        return updateActivityUseCase.updateActivity(memberId, activityId, request);
    }

    @DeleteMapping("/{activityId}")
    @Operation(summary = "활동 삭제", description = "기존 활동을 삭제합니다.")
    public ActivityIdResponse deleteActivity(
        @AuthenticationPrincipal Long memberId,
        @PathVariable Long activityId
    ) {
        return deleteActivityUseCase.deleteActivity(memberId, activityId);
    }

    @GetMapping
    @Operation(summary = "활동 통합 조회", description = "scope, category, cursor 조건으로 활동 목록을 조회합니다.")
    public ActivityListResponse getActivities(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false) Long cursor
    ) {
        return getActivityListUseCase.getActivityList(memberId, category, scope, limit, cursor);
    }

    @GetMapping("/search")
    @Operation(summary = "활동 검색", description = "활동 제목을 기준으로 검색합니다. (부분 일치)")
    public ActivityListResponse searchActivities(
        @AuthenticationPrincipal Long memberId,
        @RequestParam String keyword,
        @RequestParam(required = false, defaultValue = "20") Integer limit,
        @RequestParam(required = false) Long cursor
    ) {
        return searchActivityUseCase.searchActivities(memberId, keyword, limit, cursor);
    }

    @GetMapping("/{activityId}/details")
    @Operation(summary = "활동 상세 조회", description = "활동 상세 정보를 조회합니다.")
    public ActivityDetailResponse getActivityDetail(
        @AuthenticationPrincipal Long memberId,
        @PathVariable Long activityId
    ) {
        return getActivityDetailUseCase.getActivityDetail(memberId, activityId);
    }

    @PostMapping("/{activityId}/members")
    @Operation(summary = "활동 참여", description = "개인 또는 그룹으로 활동에 참여합니다.")
    public ActivityIdResponse joinActivity(
        @AuthenticationPrincipal Long memberId,
        @PathVariable Long activityId,
        @Valid @RequestBody JoinActivityRequest request
    ) {
        return joinActivityUseCase.joinActivity(memberId, activityId, request);
    }

    @DeleteMapping("/{activityId}/members")
    @Operation(summary = "활동 탈퇴", description = "참여 중인 활동에서 탈퇴합니다.")
    public ActivityIdResponse leaveActivity(
        @AuthenticationPrincipal Long memberId,
        @PathVariable Long activityId
    ) {
        return leaveActivityUseCase.leaveActivity(memberId, activityId);
    }

    @PostMapping("/{activityId}/likes")
    @Operation(summary = "활동 좋아요", description = "활동 좋아요를 추가합니다.")
    public ActivityIdResponse likeActivity(
        @AuthenticationPrincipal Long memberId,
        @PathVariable Long activityId
    ) {
        return likeActivityUseCase.likeActivity(memberId, activityId);
    }

    @DeleteMapping("/{activityId}/likes")
    @Operation(summary = "활동 좋아요 취소", description = "활동 좋아요를 취소합니다.")
    public ActivityIdResponse unlikeActivity(
        @AuthenticationPrincipal Long memberId,
        @PathVariable Long activityId
    ) {
        return unlikeActivityUseCase.unlikeActivity(memberId, activityId);
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "활동 이미지 업로드", description = "활동 이미지를 업로드하고 저장 경로를 반환합니다.")
    public ActivityImageUploadResponse uploadActivityImage(
        @AuthenticationPrincipal Long memberId,
        @RequestPart("file") MultipartFile file
    ) {
        return uploadActivityImageUseCase.uploadActivityImage(memberId, file);
    }
}
