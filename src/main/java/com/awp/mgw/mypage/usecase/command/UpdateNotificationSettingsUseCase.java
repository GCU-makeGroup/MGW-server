package com.awp.mgw.mypage.usecase.command;

import com.awp.mgw.mypage.controller.dto.request.UpdateNotificationSettingsRequest;

public interface UpdateNotificationSettingsUseCase {
    void updateNotificationSettings(Long memberId, UpdateNotificationSettingsRequest request);
}
