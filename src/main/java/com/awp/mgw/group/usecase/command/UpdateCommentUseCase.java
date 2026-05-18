package com.awp.mgw.group.usecase.command;

import com.awp.mgw.group.controller.dto.request.UpdateCommentRequest;

public interface UpdateCommentUseCase {
    void updateComment(Long memberId, Long groupId, Long commentId, UpdateCommentRequest request);
}
