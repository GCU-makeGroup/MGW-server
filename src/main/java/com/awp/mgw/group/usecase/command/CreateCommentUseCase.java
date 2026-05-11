package com.awp.mgw.group.usecase.command;

import com.awp.mgw.group.controller.dto.request.CreateCommentRequest;
import com.awp.mgw.group.controller.dto.response.CreateCommentResponse;

public interface CreateCommentUseCase {
    CreateCommentResponse createComment(Long memberId, Long groupId, CreateCommentRequest request);
}
