package com.awp.mgw.group.service;

import com.awp.mgw.category.port.CategoryRepository;
import com.awp.mgw.group.domain.Group;
import com.awp.mgw.group.domain.GroupMember;
import com.awp.mgw.group.domain.exception.GroupDomainException;
import com.awp.mgw.group.domain.exception.GroupErrorCode;
import com.awp.mgw.group.port.GroupCategoryRepository;
import com.awp.mgw.group.port.GroupMemberRepository;
import com.awp.mgw.group.port.GroupRepository;
import com.awp.mgw.member.domain.Member;
import com.awp.mgw.member.port.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupCommandServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private GroupCategoryRepository groupCategoryRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private GroupCommandService groupCommandService;

    @Test
    void joinGroupSavesMemberWhenGroupHasCapacity() {
        Member member = member(2L);
        Group group = group(10L, member(1L), true, 3);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(groupRepository.findByIdForUpdate(group.getId())).thenReturn(Optional.of(group));
        when(groupMemberRepository.existsByMember_IdAndGroup_Id(member.getId(), group.getId())).thenReturn(false);
        when(groupMemberRepository.countByGroup_Id(group.getId())).thenReturn(2L);

        groupCommandService.joinGroup(member.getId(), group.getId());

        verify(groupMemberRepository).save(org.mockito.ArgumentMatchers.any(GroupMember.class));
    }

    @Test
    void joinGroupThrowsWhenAlreadyJoined() {
        Member member = member(2L);
        Group group = group(10L, member(1L), true, 3);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(groupRepository.findByIdForUpdate(group.getId())).thenReturn(Optional.of(group));
        when(groupMemberRepository.existsByMember_IdAndGroup_Id(member.getId(), group.getId())).thenReturn(true);

        assertThatThrownBy(() -> groupCommandService.joinGroup(member.getId(), group.getId()))
                .isInstanceOf(GroupDomainException.class)
                .hasMessage(GroupErrorCode.MEMBER_ALREADY_JOINED_GROUP.getMessage());

        verify(groupMemberRepository, never()).save(org.mockito.ArgumentMatchers.any(GroupMember.class));
    }

    @Test
    void joinGroupThrowsWhenGroupIsFull() {
        Member member = member(2L);
        Group group = group(10L, member(1L), true, 3);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(groupRepository.findByIdForUpdate(group.getId())).thenReturn(Optional.of(group));
        when(groupMemberRepository.existsByMember_IdAndGroup_Id(member.getId(), group.getId())).thenReturn(false);
        when(groupMemberRepository.countByGroup_Id(group.getId())).thenReturn(3L);

        assertThatThrownBy(() -> groupCommandService.joinGroup(member.getId(), group.getId()))
                .isInstanceOf(GroupDomainException.class)
                .hasMessage(GroupErrorCode.GROUP_CAPACITY_EXCEEDED.getMessage());

        verify(groupMemberRepository, never()).save(org.mockito.ArgumentMatchers.any(GroupMember.class));
    }

    @Test
    void joinGroupThrowsWhenGroupIsPrivate() {
        Member member = member(2L);
        Group group = group(10L, member(1L), false, 3);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(groupRepository.findByIdForUpdate(group.getId())).thenReturn(Optional.of(group));

        assertThatThrownBy(() -> groupCommandService.joinGroup(member.getId(), group.getId()))
                .isInstanceOf(GroupDomainException.class)
                .hasMessage(GroupErrorCode.PRIVATE_GROUP_CANNOT_JOIN.getMessage());

        verify(groupMemberRepository, never()).save(org.mockito.ArgumentMatchers.any(GroupMember.class));
    }

    private Member member(Long id) {
        Member member = Member.create("member" + id + "@test.com", "member" + id, null, null);
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private Group group(Long id, Member owner, boolean isPublic, int capacity) {
        Group group = Group.create("group", "title", "content", owner, null, isPublic, capacity);
        ReflectionTestUtils.setField(group, "id", id);
        return group;
    }
}
