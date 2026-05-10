package com.awp.mgw.group.controller;

import com.awp.mgw.group.controller.dto.request.CreateGroupRequest;
import com.awp.mgw.group.controller.dto.response.CreateGroupResponse;
import com.awp.mgw.group.controller.dto.response.GetGroupDetailResponse;
import com.awp.mgw.group.controller.dto.response.GetGroupListResponse;
import com.awp.mgw.group.usecase.command.CreateGroupUseCase;
import com.awp.mgw.group.usecase.command.UpdateGroupUseCase;
import com.awp.mgw.group.usecase.query.GetGroupDetailUseCase;
import com.awp.mgw.group.usecase.query.GetGroupListUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Tag(name = "Group | 그룹", description = "그룹 관련 API")
public class GroupController {

    private final CreateGroupUseCase createGroupUseCase;
    private final UpdateGroupUseCase updateGroupUseCase;
    private final GetGroupListUseCase getGroupListUseCase;
    private final GetGroupDetailUseCase getGroupDetailUseCase;

    @PostMapping
    public CreateGroupResponse createGroup(
        @RequestParam Long memberId,
        @Valid @RequestBody CreateGroupRequest request
    ) {
        return createGroupUseCase.createGroup(memberId, request);
    }

    @PutMapping("/{groupId}")
    public CreateGroupResponse updateGroup(
        @RequestParam Long memberId,
        @PathVariable Long groupId,
        @Valid @RequestBody CreateGroupRequest request
    ) {
        return updateGroupUseCase.updateGroup(memberId, groupId, request);
    }

    @GetMapping
    public GetGroupListResponse getGroupList(
            @RequestParam Long memberId,
            // 값이 없으면 전체 그룹 조회, 값이 있으면 해당 카테고리에 속한 그룹만 조회
            @RequestParam(required = false) List<Long> categoryIds,
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return getGroupListUseCase.getGroupList(memberId, categoryIds, pageable);
    }

    @GetMapping("/{groupId}")
    public GetGroupDetailResponse getGroupDetail(
            @RequestParam Long memberId,
            @PathVariable Long groupId
    ) {
        return getGroupDetailUseCase.getGroupDetail(memberId, groupId);
    }
}
