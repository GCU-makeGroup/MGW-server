package com.awp.mgw.group.service;

import com.awp.mgw.common.exception.CommonException;
import com.awp.mgw.global.exception.constant.CommonErrorCode;
import com.awp.mgw.group.dto.GroupMemberResponse;
import com.awp.mgw.group.dto.GroupResponse;
import com.awp.mgw.group.port.GroupMemberRepository;
import com.awp.mgw.group.port.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupQueryService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupResponse getGroup(Long groupId) {
        return groupRepository.findSummaryById(groupId)
            .map(GroupResponse::from)
            .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "그룹을 찾을 수 없습니다."));
    }

    public List<GroupResponse> getGroups(Boolean isPublic) {
        return groupRepository.findAllSummaries(isPublic).stream()
            .map(GroupResponse::from)
            .toList();
    }

    public List<GroupMemberResponse> getGroupMembers(Long groupId) {
        if (groupRepository.countById(groupId) == 0) {
            throw new CommonException(CommonErrorCode.NOT_FOUND, "그룹을 찾을 수 없습니다.");
        }

        return groupMemberRepository.findMembersByGroupId(groupId).stream()
            .map(GroupMemberResponse::from)
            .toList();
    }
}
