package com.awp.mgw.group.controller;

import com.awp.mgw.group.dto.GroupMemberResponse;
import com.awp.mgw.group.dto.GroupRequest;
import com.awp.mgw.group.dto.GroupResponse;
import com.awp.mgw.group.service.GroupCommandService;
import com.awp.mgw.group.service.GroupQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Tag(name = "Group | 그룹", description = "그룹 관련 API")
public class GroupController {

    private final GroupCommandService groupCommandService;
    private final GroupQueryService groupQueryService;

    @PostMapping
    public GroupResponse createGroup(@Valid @RequestBody GroupRequest.Create request) {
        return groupCommandService.create(request);
    }

    @GetMapping("/{groupId}")
    public GroupResponse getGroup(@PathVariable Long groupId) {
        return groupQueryService.getGroup(groupId);
    }

    @GetMapping
    public List<GroupResponse> getGroups(@RequestParam(required = false) Boolean isPublic) {
        return groupQueryService.getGroups(isPublic);
    }

    @PutMapping("/{groupId}")
    public GroupResponse updateGroup(
        @PathVariable Long groupId,
        @Valid @RequestBody GroupRequest.Update request
    ) {
        return groupCommandService.update(groupId, request);
    }

    @DeleteMapping("/{groupId}")
    public void deleteGroup(@PathVariable Long groupId) {
        groupCommandService.delete(groupId);
    }

    @PostMapping("/{groupId}/members/{memberId}")
    public void addGroupMember(@PathVariable Long groupId, @PathVariable Long memberId) {
        groupCommandService.addMember(groupId, memberId);
    }

    @GetMapping("/{groupId}/members")
    public List<GroupMemberResponse> getGroupMembers(@PathVariable Long groupId) {
        return groupQueryService.getGroupMembers(groupId);
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    public void removeGroupMember(@PathVariable Long groupId, @PathVariable Long memberId) {
        groupCommandService.removeMember(groupId, memberId);
    }
}
