package com.awp.mgw.group.service;

import com.awp.mgw.common.exception.CommonException;
import com.awp.mgw.global.exception.constant.CommonErrorCode;
import com.awp.mgw.group.dto.GroupRequest;
import com.awp.mgw.group.dto.GroupResponse;
import com.awp.mgw.group.port.GroupMemberRepository;
import com.awp.mgw.group.port.GroupRepository;
import com.awp.mgw.member.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupCommandService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;

    public GroupResponse create(GroupRequest.Create request) {
        groupRepository.insert(request.title(), request.content(), request.isPublic());
        Long groupId = groupRepository.findLastInsertId();
        return findGroup(groupId);
    }

    public GroupResponse update(Long groupId, GroupRequest.Update request) {
        assertGroupExists(groupId);
        int updated = groupRepository.update(groupId, request.title(), request.content(), request.isPublic());
        if (updated == 0) {
            throw new CommonException(CommonErrorCode.NOT_FOUND, "수정할 그룹을 찾을 수 없습니다.");
        }
        return findGroup(groupId);
    }

    public void delete(Long groupId) {
        assertGroupExists(groupId);
        groupMemberRepository.deleteAllByGroupId(groupId);
        int deleted = groupRepository.deleteById(groupId);
        if (deleted == 0) {
            throw new CommonException(CommonErrorCode.NOT_FOUND, "삭제할 그룹을 찾을 수 없습니다.");
        }
    }

    public void addMember(Long groupId, Long memberId) {
        assertGroupExists(groupId);
        assertMemberExists(memberId);
        if (groupMemberRepository.countByGroupIdAndMemberId(groupId, memberId) > 0) {
            throw new CommonException(CommonErrorCode.BAD_REQUEST, "이미 그룹에 참여한 멤버입니다.");
        }
        groupMemberRepository.insert(groupId, memberId);
    }

    public void removeMember(Long groupId, Long memberId) {
        assertGroupExists(groupId);
        assertMemberExists(memberId);
        int deleted = groupMemberRepository.delete(groupId, memberId);
        if (deleted == 0) {
            throw new CommonException(CommonErrorCode.NOT_FOUND, "그룹에 속한 멤버를 찾을 수 없습니다.");
        }
    }

    private void assertGroupExists(Long groupId) {
        if (groupRepository.countById(groupId) == 0) {
            throw new CommonException(CommonErrorCode.NOT_FOUND, "그룹을 찾을 수 없습니다.");
        }
    }

    private void assertMemberExists(Long memberId) {
        if (memberRepository.countActiveById(memberId) == 0) {
            throw new CommonException(CommonErrorCode.NOT_FOUND, "멤버를 찾을 수 없습니다.");
        }
    }

    private GroupResponse findGroup(Long groupId) {
        return groupRepository.findSummaryById(groupId)
            .map(GroupResponse::from)
            .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "그룹을 찾을 수 없습니다."));
    }
}
