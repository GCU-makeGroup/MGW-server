package com.awp.mgw.group.service;

import com.awp.mgw.activity.port.ActivityGroupRepository;
import com.awp.mgw.category.port.CategoryRepository;
import com.awp.mgw.group.controller.dto.request.CreateCommentRequest;
import com.awp.mgw.group.controller.dto.response.CreateCommentResponse;
import com.awp.mgw.group.domain.Comment;
import com.awp.mgw.group.domain.Group;
import com.awp.mgw.group.domain.GroupMember;
import com.awp.mgw.group.domain.exception.GroupDomainException;
import com.awp.mgw.group.domain.exception.GroupErrorCode;
import com.awp.mgw.group.port.CommentRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupCommandServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private GroupCategoryRepository groupCategoryRepository;

    @Mock
    private ActivityGroupRepository activityGroupRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private GroupCommandService groupCommandService;

    @Test
    void createCommentAllowsNonGroupMember() {
        Member member = member(2L);
        Group group = group(10L, member(1L));
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupMemberRepository.existsByMember_IdAndGroup_Id(member.getId(), group.getId())).thenReturn(false);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            ReflectionTestUtils.setField(comment, "id", 100L);
            return comment;
        });

        CreateCommentResponse response = groupCommandService.createComment(
                member.getId(),
                group.getId(),
                new CreateCommentRequest("hello", null)
        );

        verify(commentRepository).save(any(Comment.class));
        assertThat(response.commentId()).isEqualTo(100L);
        assertThat(response.authorGroupMember()).isFalse();
    }

    @Test
    void createCommentReturnsAuthorGroupMemberWhenWriterBelongsToGroup() {
        Member member = member(2L);
        Group group = group(10L, member(1L));
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupMemberRepository.existsByMember_IdAndGroup_Id(member.getId(), group.getId())).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            ReflectionTestUtils.setField(comment, "id", 101L);
            return comment;
        });

        CreateCommentResponse response = groupCommandService.createComment(
                member.getId(),
                group.getId(),
                new CreateCommentRequest("member comment", null)
        );

        verify(commentRepository).save(any(Comment.class));
        assertThat(response.commentId()).isEqualTo(101L);
        assertThat(response.authorGroupMember()).isTrue();
    }

    @Test
    void createCommentThrowsWhenParentCommentBelongsToOtherGroup() {
        Member member = member(2L);
        Group group = group(10L, member(1L));
        Group otherGroup = group(20L, member(3L));
        Comment parent = comment(100L, otherGroup, member);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(commentRepository.findById(parent.getId())).thenReturn(Optional.of(parent));

        assertThatThrownBy(() -> groupCommandService.createComment(
                member.getId(),
                group.getId(),
                new CreateCommentRequest("reply", parent.getId())
        ))
                .isInstanceOf(GroupDomainException.class)
                .hasMessage(GroupErrorCode.INVALID_COMMENT_PARENT.getMessage());

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void joinGroupSavesMemberWhenGroupHasCapacity() {
        Member member = member(2L);
        Group group = group(10L, member(1L), true, 3);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(groupRepository.findByIdForUpdate(group.getId())).thenReturn(Optional.of(group));
        when(groupMemberRepository.existsByMember_IdAndGroup_Id(member.getId(), group.getId())).thenReturn(false);
        when(groupMemberRepository.countByGroup_Id(group.getId())).thenReturn(2L);

        groupCommandService.joinGroup(member.getId(), group.getId());

        verify(groupMemberRepository).save(any(GroupMember.class));
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

        verify(groupMemberRepository, never()).save(any(GroupMember.class));
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

        verify(groupMemberRepository, never()).save(any(GroupMember.class));
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

        verify(groupMemberRepository, never()).save(any(GroupMember.class));
    }

    @Test
    void deleteGroupDeletesOnlyWhenRequesterIsOwner() {
        Member owner = member(1L);
        Group group = group(10L, owner);
        when(memberRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

        groupCommandService.deleteGroup(owner.getId(), group.getId());

        verify(activityGroupRepository).deleteAllByGroup(group);
        verify(groupRepository).delete(group);
    }

    @Test
    void deleteGroupThrowsWhenRequesterIsNotOwner() {
        Member owner = member(1L);
        Member requester = member(2L);
        Group group = group(10L, owner);
        when(memberRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

        assertThatThrownBy(() -> groupCommandService.deleteGroup(requester.getId(), group.getId()))
                .isInstanceOf(GroupDomainException.class)
                .hasMessage(GroupErrorCode.GROUP_NOT_OWNED.getMessage());

        verify(activityGroupRepository, never()).deleteAllByGroup(group);
        verify(groupRepository, never()).delete(group);
    }

    @Test
    void leaveGroupDeletesMembershipWhenRequesterIsNormalMember() {
        Member owner = member(1L);
        Member requester = member(2L);
        Group group = group(10L, owner);
        GroupMember groupMember = GroupMember.create(requester, group);
        when(memberRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupMemberRepository.findByMember_IdAndGroup_Id(requester.getId(), group.getId()))
                .thenReturn(Optional.of(groupMember));

        groupCommandService.leaveGroup(requester.getId(), group.getId());

        verify(groupMemberRepository).delete(groupMember);
        assertThat(group.getMember()).isEqualTo(owner);
    }

    @Test
    void leaveGroupThrowsWhenOwnerLeavesBeforeOtherMembers() {
        Member owner = member(1L);
        Group group = group(10L, owner);
        GroupMember groupMember = GroupMember.create(owner, group);
        when(memberRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupMemberRepository.findByMember_IdAndGroup_Id(owner.getId(), group.getId()))
                .thenReturn(Optional.of(groupMember));
        when(groupMemberRepository.countByGroup_Id(group.getId())).thenReturn(2L);

        assertThatThrownBy(() -> groupCommandService.leaveGroup(owner.getId(), group.getId()))
                .isInstanceOf(GroupDomainException.class)
                .hasMessage(GroupErrorCode.GROUP_OWNER_CANNOT_LEAVE.getMessage());

        verify(groupMemberRepository, never()).delete(groupMember);
        assertThat(group.getMember()).isEqualTo(owner);
    }

    @Test
    void leaveGroupAllowsOwnerWhenOwnerIsLastMember() {
        Member owner = member(1L);
        Group group = group(10L, owner);
        GroupMember groupMember = GroupMember.create(owner, group);
        when(memberRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupMemberRepository.findByMember_IdAndGroup_Id(owner.getId(), group.getId()))
                .thenReturn(Optional.of(groupMember));
        when(groupMemberRepository.countByGroup_Id(group.getId())).thenReturn(1L);

        groupCommandService.leaveGroup(owner.getId(), group.getId());

        verify(groupMemberRepository).delete(groupMember);
        assertThat(group.getMember()).isNull();
    }

    @Test
    void leaveGroupThrowsWhenRequesterIsNotGroupMember() {
        Member requester = member(2L);
        Group group = group(10L, member(1L));
        when(memberRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupMemberRepository.findByMember_IdAndGroup_Id(requester.getId(), group.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupCommandService.leaveGroup(requester.getId(), group.getId()))
                .isInstanceOf(GroupDomainException.class)
                .hasMessage(GroupErrorCode.GROUP_MEMBER_NOT_FOUND.getMessage());
    }

    private Member member(Long id) {
        Member member = Member.create("member" + id + "@test.com", "password", "member" + id, null, null);
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private Group group(Long id, Member owner) {
        return group(id, owner, true, 10);
    }

    private Group group(Long id, Member owner, boolean isPublic, int capacity) {
        Group group = Group.create("group", "title", "content", owner, null, isPublic, capacity);
        ReflectionTestUtils.setField(group, "id", id);
        return group;
    }

    private Comment comment(Long id, Group group, Member member) {
        Comment comment = Comment.create(group, member, null, "content");
        ReflectionTestUtils.setField(comment, "id", id);
        return comment;
    }
}
