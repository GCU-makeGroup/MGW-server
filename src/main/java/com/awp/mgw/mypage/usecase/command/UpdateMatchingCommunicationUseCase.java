package com.awp.mgw.mypage.usecase.command;

import com.awp.mgw.mypage.controller.dto.request.UpdateMatchingCommunicationRequest;

public interface UpdateMatchingCommunicationUseCase {
    void updateMatchingCommunication(Long memberId, UpdateMatchingCommunicationRequest request);
}
