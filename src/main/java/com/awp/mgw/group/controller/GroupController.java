package com.awp.mgw.group.controller;

import com.awp.mgw.group.controller.dto.request.CreateCommentRequest;
import com.awp.mgw.group.controller.dto.request.CreateGroupRequest;
import com.awp.mgw.group.controller.dto.request.UpdateCommentRequest;
import com.awp.mgw.group.controller.dto.response.CreateCommentResponse;
import com.awp.mgw.group.controller.dto.response.CreateGroupResponse;
import com.awp.mgw.group.controller.dto.response.GetGroupDetailResponse;
import com.awp.mgw.group.controller.dto.response.GetGroupListResponse;
import com.awp.mgw.group.usecase.command.CreateCommentUseCase;
import com.awp.mgw.group.usecase.command.CreateGroupUseCase;
import com.awp.mgw.group.usecase.command.DeleteCommentUseCase;
import com.awp.mgw.group.usecase.command.DeleteGroupUseCase;
import com.awp.mgw.group.usecase.command.LeaveGroupUseCase;
import com.awp.mgw.group.usecase.command.JoinGroupUseCase;
import com.awp.mgw.group.usecase.command.UpdateCommentUseCase;
import com.awp.mgw.group.usecase.command.UpdateGroupUseCase;
import com.awp.mgw.group.usecase.query.GetGroupDetailUseCase;
import com.awp.mgw.group.usecase.query.GetGroupListUseCase;
import com.awp.mgw.group.usecase.query.GetMyGroupListUseCase;
import com.awp.mgw.group.usecase.query.SearchGroupListUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Validated
@Tag(name = "Group | 그룹", description = "그룹 관련 API")
public class GroupController {

    private final CreateGroupUseCase createGroupUseCase;
    private final CreateCommentUseCase createCommentUseCase;
    private final UpdateGroupUseCase updateGroupUseCase;
    private final JoinGroupUseCase joinGroupUseCase;
    private final DeleteGroupUseCase deleteGroupUseCase;
    private final LeaveGroupUseCase leaveGroupUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;
    private final UpdateCommentUseCase updateCommentUseCase;
    private final GetGroupListUseCase getGroupListUseCase;
    private final GetMyGroupListUseCase getMyGroupListUseCase;
    private final SearchGroupListUseCase searchGroupListUseCase;
    private final GetGroupDetailUseCase getGroupDetailUseCase;

    @PostMapping
    @Operation(
            summary = "그룹 생성",
            description = "회원이 그룹 모집글을 생성합니다. 생성자는 그룹 작성자이자 최초 그룹 멤버로 등록됩니다."
    )
    public CreateGroupResponse createGroup(
        @AuthenticationPrincipal Long memberId,
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
        @AuthenticationPrincipal Long memberId,
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
            @AuthenticationPrincipal Long memberId,
            // 값이 없으면 전체 그룹 조회, 값이 있으면 해당 카테고리에 속한 그룹만 조회
            @Parameter(description = "필터링할 카테고리 ID 목록. 없으면 전체 조회", example = "1")
            @RequestParam(required = false) List<Long> categoryIds,
            @Parameter(description = "페이징 및 정렬 정보. 예: sort=createdAt,desc / sort=name,asc / sort=capacity,desc")
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return getGroupListUseCase.getGroupList(memberId, categoryIds, pageable);
    }

    @GetMapping("/me")
    @Operation(
            summary = "내가 속한 그룹 목록 조회",
            description = "요청 회원이 group_member로 참여 중인 그룹 목록을 조회합니다. 응답 형식은 그룹 목록 조회와 동일합니다."
    )
    public GetGroupListResponse getMyGroupList(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "페이징 및 정렬 정보. 예: sort=createdAt,desc / sort=name,asc / sort=capacity,desc")
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return getMyGroupListUseCase.getMyGroupList(memberId, pageable);
    }

    @GetMapping("/search")
    @Operation(
            summary = "그룹명 검색",
            description = "그룹명 기준으로 검색합니다. 검색어와 그룹명의 공백을 제거한 뒤 비교하므로 띄어쓰기가 달라도 검색됩니다."
    )
    public GetGroupListResponse searchGroupList(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "검색할 그룹명 키워드", example = "모 각 코")
            @RequestParam
            @NotBlank(message = "검색어는 필수입니다.")
            @Size(max = 50, message = "검색어는 50자 이하여야 합니다.")
            String keyword,
            @Parameter(description = "페이징 및 정렬 정보. 예: sort=updatedAt,desc / sort=name,asc")
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return searchGroupListUseCase.searchGroupList(memberId, keyword, pageable);
    }

    @GetMapping("/{groupId}")
    @Operation(
            summary = "그룹 상세 조회",
            description = "공개 그룹 또는 본인이 작성한 비공개 그룹을 상세 조회합니다. 그룹 정보, 현재 인원 수, 댓글 목록을 함께 반환합니다."
    )
    public GetGroupDetailResponse getGroupDetail(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "조회할 그룹 ID", example = "10")
            @PathVariable Long groupId
    ) {
        return getGroupDetailUseCase.getGroupDetail(memberId, groupId);
    }

    @PostMapping("/{groupId}/members/me")
    @Operation(
            summary = "그룹 참여",
            description = "공개 그룹에 참여합니다. 이미 참여한 그룹이거나 정원이 초과된 그룹, 비공개 그룹은 참여할 수 없습니다."
    )
    public void joinGroup(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "참여할 그룹 ID", example = "10")
            @PathVariable Long groupId
    ) {
        joinGroupUseCase.joinGroup(memberId, groupId);
    }

    @PostMapping("/{groupId}/comments")
    @Operation(
            summary = "댓글 작성",
            description = "회원이 그룹에 댓글을 작성합니다. parentId를 전달하면 같은 그룹 댓글의 대댓글로 작성됩니다."
    )
    public CreateCommentResponse createComment(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "댓글을 작성할 그룹 ID", example = "10")
            @PathVariable Long groupId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return createCommentUseCase.createComment(memberId, groupId, request);
    }

    @PutMapping("/{groupId}/comments/{commentId}")
    @Operation(
            summary = "댓글 수정",
            description = "댓글 작성자만 수정할 수 있습니다."
    )
    public void updateComment(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "그룹 ID", example = "10")
            @PathVariable Long groupId,
            @Parameter(description = "수정할 댓글 ID", example = "5")
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request
    ) {
        updateCommentUseCase.updateComment(memberId, groupId, commentId, request);
    }

    @DeleteMapping("/{groupId}/comments/{commentId}")
    @Operation(
            summary = "댓글 삭제",
            description = "댓글 작성자만 삭제할 수 있습니다."
    )
    public void deleteComment(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "그룹 ID", example = "10")
            @PathVariable Long groupId,
            @Parameter(description = "삭제할 댓글 ID", example = "5")
            @PathVariable Long commentId
    ) {
        deleteCommentUseCase.deleteComment(memberId, groupId, commentId);
    }

    @DeleteMapping("/{groupId}")
    @Operation(
            summary = "그룹 삭제",
            description = "그룹 작성자만 삭제할 수 있습니다. 삭제 시 그룹 댓글, 그룹 멤버, 그룹 카테고리, 활동-그룹 매핑이 함께 제거됩니다."
    )
    public void deleteGroup(
            @AuthenticationPrincipal Long memberId,
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
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "탈퇴할 그룹 ID", example = "10")
            @PathVariable Long groupId
    ) {
        leaveGroupUseCase.leaveGroup(memberId, groupId);
    }
}
