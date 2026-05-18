package com.awp.mgw.group.usecase.command;

public interface DeleteCommentUseCase {
    void deleteComment(Long memberId, Long groupId, Long commentId);
}
