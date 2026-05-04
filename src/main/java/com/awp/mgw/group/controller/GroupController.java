package com.awp.mgw.group.controller;

import com.awp.mgw.group.controller.dto.request.CreateGroupRequest;
import com.awp.mgw.group.controller.dto.response.CreateGroupResponse;
import com.awp.mgw.group.usecase.CreateGroupUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Tag(name = "Group | 그룹", description = "그룹 관련 API")
public class GroupController {

    private final CreateGroupUseCase createGroupUseCase;

    @PostMapping
    public CreateGroupResponse createGroup(
        @RequestParam Long memberId,
        @Valid @RequestBody CreateGroupRequest request
    ) {
        return createGroupUseCase.createGroup(memberId, request);
    }
}
