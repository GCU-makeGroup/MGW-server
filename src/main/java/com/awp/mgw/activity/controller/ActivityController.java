package com.awp.mgw.activity.controller;

import com.awp.mgw.activity.controller.dto.request.CreateActivityRequest;
import com.awp.mgw.activity.controller.dto.request.UpdateActivityRequest;
import com.awp.mgw.activity.controller.dto.response.ActivityIdResponse;
import com.awp.mgw.activity.usecase.CreateActivityUseCase;
import com.awp.mgw.activity.usecase.DeleteActivityUseCase;
import com.awp.mgw.activity.usecase.UpdateActivityUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
@Tag(name = "Activity | 활동", description = "활동 관련 API")
public class ActivityController {

    private final CreateActivityUseCase createActivityUseCase;
    private final UpdateActivityUseCase updateActivityUseCase;
    private final DeleteActivityUseCase deleteActivityUseCase;

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
}
