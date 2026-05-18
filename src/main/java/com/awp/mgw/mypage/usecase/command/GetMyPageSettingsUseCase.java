package com.awp.mgw.mypage.usecase.command;

import com.awp.mgw.mypage.controller.dto.response.MyPageSettingsResponse;

public interface GetMyPageSettingsUseCase {
    MyPageSettingsResponse getSettings(Long memberId);
}
