package com.awp.mgw.member.usecase;

import com.awp.mgw.member.controller.dto.request.ChangePasswordRequest;

public interface ChangePasswordUseCase {
    void changePassword(Long memberId, ChangePasswordRequest request);
}
