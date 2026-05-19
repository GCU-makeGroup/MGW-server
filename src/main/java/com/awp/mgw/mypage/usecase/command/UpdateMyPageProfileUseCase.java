package com.awp.mgw.mypage.usecase.command;

import com.awp.mgw.mypage.controller.dto.request.UpdateProfileRequest;

public interface UpdateMyPageProfileUseCase {
    void updateProfile(Long memberId, UpdateProfileRequest request);
}
