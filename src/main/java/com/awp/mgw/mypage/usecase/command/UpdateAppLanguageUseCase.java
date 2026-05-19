package com.awp.mgw.mypage.usecase.command;

import com.awp.mgw.mypage.controller.dto.request.UpdateAppLanguageRequest;

public interface UpdateAppLanguageUseCase {
    void updateAppLanguage(Long memberId, UpdateAppLanguageRequest request);
}
