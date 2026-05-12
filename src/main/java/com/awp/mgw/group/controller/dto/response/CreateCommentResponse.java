package com.awp.mgw.group.controller.dto.response;

public record CreateCommentResponse(
        Long commentId,
        Boolean authorGroupMember
) {
    public static CreateCommentResponse from(Long commentId, Boolean authorGroupMember) {
        return new CreateCommentResponse(commentId, authorGroupMember);
    }
}
