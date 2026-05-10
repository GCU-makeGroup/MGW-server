package com.awp.mgw.group.controller;

import com.awp.mgw.group.controller.dto.request.CreateGroupRequest;
import com.awp.mgw.group.controller.dto.response.CreateGroupResponse;
import com.awp.mgw.group.controller.dto.response.GetGroupDetailResponse;
import com.awp.mgw.group.controller.dto.response.GetGroupListResponse;
import com.awp.mgw.group.usecase.command.CreateGroupUseCase;
import com.awp.mgw.group.usecase.command.DeleteGroupUseCase;
import com.awp.mgw.group.usecase.command.LeaveGroupUseCase;
import com.awp.mgw.group.usecase.command.UpdateGroupUseCase;
import com.awp.mgw.group.usecase.query.GetGroupDetailUseCase;
import com.awp.mgw.group.usecase.query.GetGroupListUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    private final DeleteGroupUseCase deleteGroupUseCase;
    private final LeaveGroupUseCase leaveGroupUseCase;
    private final GetGroupListUseCase getGroupListUseCase;
    private final GetGroupDetailUseCase getGroupDetailUseCase;

    @PostMapping
    @Operation(
            summary = "그룹 생성",
            description = "회원이 그룹 모집글을 생성합니다. 생성자는 그룹 작성자이자 최초 그룹 멤버로 등록됩니다."
    )
    public CreateGroupResponse createGroup(
        @Parameter(description = "그룹을 생성하는 회원 ID", example = "1")
        @RequestParam Long memberId,
        @Valid @RequestBody CreateGroupRequest request
    ) {
        return createGroupUseCase.createGroup(memberId, request);
    }

    @PutMapping("/{groupId}")
    @Operation(
            summary = "그룹 수정",
            description = "그룹 작성자만 그룹 모집글을 수정할 수 있습니다."
    )
    public CreateGroupResponse updateGroup(
        @Parameter(description = "수정 요청 회원 ID", example = "1")
        @RequestParam Long memberId,
        @Parameter(description = "수정할 그룹 ID", example = "10")
        @PathVariable Long groupId,
        @Valid @RequestBody CreateGroupRequest request
    ) {
        return updateGroupUseCase.updateGroup(memberId, groupId, request);
    }

    @GetMapping
    @Operation(
            summary = "그룹 목록 조회",
            description = "공개 그룹과 본인이 작성한 비공개 그룹을 조회합니다. categoryIds가 없으면 전체 조회하며, 정렬은 createdAt, updatedAt, name, title, capacity, id를 지원합니다."
    )
    public GetGroupListResponse getGroupList(
            @Parameter(description = "조회 요청 회원 ID", example = "1")
            @RequestParam Long memberId,
            // 값이 없으면 전체 그룹 조회, 값이 있으면 해당 카테고리에 속한 그룹만 조회
            @Parameter(description = "필터링할 카테고리 ID 목록. 없으면 전체 조회", example = "1")
            @RequestParam(required = false) List<Long> categoryIds,
            @Parameter(description = "페이징 및 정렬 정보. 예: sort=createdAt,desc / sort=name,asc / sort=capacity,desc")
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return getGroupListUseCase.getGroupList(memberId, categoryIds, pageable);
    }

    @GetMapping("/{groupId}")
    @Operation(
            summary = "그룹 상세 조회",
            description = "공개 그룹 또는 본인이 작성한 비공개 그룹을 상세 조회합니다. 그룹 정보, 현재 인원 수, 댓글 목록을 함께 반환합니다."
    )
    public GetGroupDetailResponse getGroupDetail(
            @Parameter(description = "조회 요청 회원 ID", example = "1")
            @RequestParam Long memberId,
            @Parameter(description = "조회할 그룹 ID", example = "10")
            @PathVariable Long groupId
    ) {
        return getGroupDetailUseCase.getGroupDetail(memberId, groupId);
    }

    @DeleteMapping("/{groupId}")
    @Operation(
            summary = "그룹 삭제",
            description = "그룹 작성자만 삭제할 수 있습니다. 삭제 시 그룹 댓글, 그룹 멤버, 그룹 카테고리, 활동-그룹 매핑이 함께 제거됩니다."
    )
    public void deleteGroup(
            @Parameter(description = "삭제 요청 회원 ID", example = "1")
            @RequestParam Long memberId,
            @Parameter(description = "삭제할 그룹 ID", example = "10")
            @PathVariable Long groupId
    ) {
        deleteGroupUseCase.deleteGroup(memberId, groupId);
    }

    @DeleteMapping("/{groupId}/members/me")
    @Operation(
            summary = "그룹 탈퇴",
            description = "그룹 멤버만 탈퇴할 수 있습니다. 그룹 작성자는 다른 팀원이 모두 나가고 혼자 남았을 때만 탈퇴할 수 있습니다."
    )
    public void leaveGroup(
            @Parameter(description = "탈퇴 요청 회원 ID", example = "1")
            @RequestParam Long memberId,
            @Parameter(description = "탈퇴할 그룹 ID", example = "10")
            @PathVariable Long groupId
    ) {
        leaveGroupUseCase.leaveGroup(memberId, groupId);
    }
}
