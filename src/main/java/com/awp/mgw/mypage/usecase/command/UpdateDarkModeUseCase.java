package com.awp.mgw.mypage.usecase.command;

import com.awp.mgw.mypage.controller.dto.request.UpdateDarkModeRequest;

public interface UpdateDarkModeUseCase {
    void updateDarkMode(Long memberId, UpdateDarkModeRequest request);
}
