package com.awp.mgw.group.service;

import com.awp.mgw.group.controller.dto.response.GetGroupDetailResponse;
import com.awp.mgw.group.controller.dto.response.GetGroupListResponse;
import com.awp.mgw.group.domain.Comment;
import com.awp.mgw.group.domain.Group;
import com.awp.mgw.group.port.CommentRepository;
import com.awp.mgw.group.port.GroupQueryRepository;
import com.awp.mgw.member.domain.Member;
import com.awp.mgw.member.port.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupQueryServiceTest {

    @Mock
    private GroupQueryRepository groupQueryRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private GroupQueryService groupQueryService;

    @Test
    void getGroupDetailReturnsCommentsWithAuthorGroupMemberFlag() {
        Member requester = member(1L);
        Member groupMember = member(2L);
        Member outsideMember = member(3L);
        Group group = group(10L, requester);
        Comment memberComment = comment(100L, group, groupMember, null, "member comment");
        Comment outsideComment = comment(101L, group, outsideMember, memberComment, "outside reply");

        when(memberRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(groupQueryRepository.findAccessibleGroupDetailById(group.getId(), requester.getId()))
                .thenReturn(Optional.of(group));
        when(groupQueryRepository.findCategoriesByGroupIds(List.of(group.getId()))).thenReturn(Map.of());
        when(groupQueryRepository.findCurrentMemberCountsByGroupIds(List.of(group.getId())))
                .thenReturn(Map.of(group.getId(), 2));
        when(commentRepository.countCommentsByGroupIds(List.of(group.getId()))).thenReturn(List.of(commentCount(group.getId(), 2L)));
        when(groupQueryRepository.findCommentsByGroupId(group.getId()))
                .thenReturn(List.of(memberComment, outsideComment));
        when(groupQueryRepository.findGroupMemberIdsByGroupId(group.getId()))
                .thenReturn(Set.of(requester.getId(), groupMember.getId()));

        GetGroupDetailResponse response = groupQueryService.getGroupDetail(requester.getId(), group.getId());

        assertThat(response.comments()).hasSize(2);
        assertThat(response.comments().get(0).id()).isEqualTo(memberComment.getId());
        assertThat(response.comments().get(0).authorGroupMember()).isTrue();
        assertThat(response.comments().get(1).id()).isEqualTo(outsideComment.getId());
        assertThat(response.comments().get(1).parentId()).isEqualTo(memberComment.getId());
        assertThat(response.comments().get(1).authorGroupMember()).isFalse();
        assertThat(response.commentCount()).isEqualTo(2);
        assertThat(response.currentMemberCount()).isEqualTo(2);
    }

    @Test
    void getMyGroupListReturnsGroupListResponse() {
        Member requester = member(1L);
        Group group = group(10L, requester);
        Pageable pageable = PageRequest.of(0, 10);
        when(memberRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(groupQueryRepository.findMyGroupList(requester.getId(), pageable))
                .thenReturn(new PageImpl<>(List.of(group), pageable, 1));
        when(commentRepository.countCommentsByGroupIds(List.of(group.getId()))).thenReturn(List.of(commentCount(group.getId(), 3L)));
        when(groupQueryRepository.findCurrentMemberCountsByGroupIds(List.of(group.getId())))
                .thenReturn(Map.of(group.getId(), 2));
        when(groupQueryRepository.findCategoriesByGroupIds(List.of(group.getId()))).thenReturn(Map.of());

        GetGroupListResponse response = groupQueryService.getMyGroupList(requester.getId(), pageable);

        verify(groupQueryRepository).findMyGroupList(requester.getId(), pageable);
        assertThat(response.pageInfo().totalElements()).isEqualTo(1);
        assertThat(response.groups()).hasSize(1);
        assertThat(response.groups().get(0).id()).isEqualTo(group.getId());
        assertThat(response.groups().get(0).commentCount()).isEqualTo(3);
        assertThat(response.groups().get(0).currentMemberCount()).isEqualTo(2);
    }

    private Member member(Long id) {
        Member member = Member.create("member" + id + "@test.com", "password", "member" + id, null, null);
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private Group group(Long id, Member owner) {
        Group group = Group.create("group", "title", "content", owner, null, true, 10);
        ReflectionTestUtils.setField(group, "id", id);
        return group;
    }

    private Comment comment(Long id, Group group, Member member, Comment parent, String content) {
        Comment comment = Comment.create(group, member, parent, content);
        ReflectionTestUtils.setField(comment, "id", id);
        return comment;
    }

    private CommentRepository.CommentCountProjection commentCount(Long groupId, Long commentCount) {
        return new CommentRepository.CommentCountProjection() {
            @Override
            public Long getGroupId() {
                return groupId;
            }

            @Override
            public Long getCommentCount() {
                return commentCount;
            }
        };
    }
}
