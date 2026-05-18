package com.awp.mgw.group.controller.dto.response;

import com.awp.mgw.category.domain.Category;
import com.awp.mgw.group.domain.Comment;
import com.awp.mgw.group.domain.Group;
import com.awp.mgw.member.domain.Member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record GetGroupDetailResponse(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String name,
        String title,
        String content,
        String imageUrl,
        Boolean isPublic,
        AuthorInfo author,
        List<CategoryInfo> categories,
        Integer capacity,
        Integer currentMemberCount,
        Integer commentCount,
        List<CommentInfo> comments,
        Boolean isMember
) {
    public static GetGroupDetailResponse from(
            Group group,
            List<Category> categories,
            Integer currentMemberCount,
            Integer commentCount,
            List<Comment> comments,
            Set<Long> groupMemberIds,
            Long currentMemberId
    ) {
        return new GetGroupDetailResponse(
                group.getId(),
                group.getCreatedAt(),
                group.getUpdatedAt(),
                group.getName(),
                group.getTitle(),
                group.getContent(),
                group.getImageUrl(),
                group.getIsPublic(),
                AuthorInfo.from(group.getMember()),
                categories.stream()
                        .map(CategoryInfo::from)
                        .toList(),
                group.getCapacity(),
                currentMemberCount,
                commentCount,
                comments.stream()
                        .map(comment -> CommentInfo.from(comment, groupMemberIds))
                        .toList(),
                groupMemberIds.contains(currentMemberId)
        );
    }

    public record AuthorInfo(Long id, String name, String imageUrl) {
        public static AuthorInfo from(Member member) {
            if (member == null) {
                return null;
            }

            return new AuthorInfo(member.getId(), member.getName(), member.getImageUrl());
        }
    }

    public record CategoryInfo(Long id, String name) {
        public static CategoryInfo from(Category category) {
            return new CategoryInfo(category.getId(), category.getName());
        }
    }

    public record CommentInfo(
            Long id,
            Long parentId,
            AuthorInfo author,
            Boolean authorGroupMember,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static CommentInfo from(Comment comment, Set<Long> groupMemberIds) {
            Member author = comment.getMember();

            return new CommentInfo(
                    comment.getId(),
                    comment.getParent() != null ? comment.getParent().getId() : null,
                    AuthorInfo.from(author),
                    author != null && groupMemberIds.contains(author.getId()),
                    comment.getContent(),
                    comment.getCreatedAt(),
                    comment.getUpdatedAt()
            );
        }
    }
}
