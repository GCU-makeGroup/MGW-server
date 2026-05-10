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
import com.awp.mgw.activity.usecase.UploadActivityImageUseCase;
import com.awp.mgw.activity.usecase.UpdateActivityUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
    private final UploadActivityImageUseCase uploadActivityImageUseCase;

    @PostMapping
    @Operation(summary = "활동 생성", description = "신규 활동을 생성합니다.")
    public ActivityIdResponse createActivity(
        @RequestParam Long memberId,
        @Valid @RequestBody CreateActivityRequest request
    ) {
        return createActivityUseCase.createActivity(memberId, request);
    }

    @PutMapping("/{activityId}")
    @Operation(summary = "활동 수정", description = "기존 활동을 수정합니다.")
    public ActivityIdResponse updateActivity(
        @RequestParam Long memberId,
        @PathVariable Long activityId,
        @Valid @RequestBody UpdateActivityRequest request
    ) {
        return updateActivityUseCase.updateActivity(memberId, activityId, request);
    }

    @DeleteMapping("/{activityId}")
    @Operation(summary = "활동 삭제", description = "기존 활동을 삭제합니다.")
    public ActivityIdResponse deleteActivity(
        @RequestParam Long memberId,
        @PathVariable Long activityId
    ) {
        return deleteActivityUseCase.deleteActivity(memberId, activityId);
    }

    @GetMapping
    @Operation(summary = "활동 통합 조회", description = "scope, category, cursor 조건으로 활동 목록을 조회합니다.")
    public ActivityListResponse getActivities(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) Long cursor
    ) {
        return getActivityListUseCase.getActivityList(memberId, category, scope, cursor);
    }

    @GetMapping("/{activityId}/details")
    @Operation(summary = "활동 상세 조회", description = "활동 상세 정보를 조회합니다.")
    public ActivityDetailResponse getActivityDetail(
        @RequestParam Long memberId,
        @PathVariable Long activityId
    ) {
        return getActivityDetailUseCase.getActivityDetail(memberId, activityId);
    }

    @PostMapping("/{activityId}/members")
    @Operation(summary = "활동 참여", description = "개인 또는 그룹으로 활동에 참여합니다.")
    public ActivityIdResponse joinActivity(
        @RequestParam Long memberId,
        @PathVariable Long activityId,
        @Valid @RequestBody JoinActivityRequest request
    ) {
        return joinActivityUseCase.joinActivity(memberId, activityId, request);
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "활동 이미지 업로드", description = "활동 이미지를 업로드하고 저장 경로를 반환합니다.")
    public ActivityImageUploadResponse uploadActivityImage(
        @RequestParam Long memberId,
        @RequestPart("file") MultipartFile file
    ) {
        return uploadActivityImageUseCase.uploadActivityImage(memberId, file);
    }
}
