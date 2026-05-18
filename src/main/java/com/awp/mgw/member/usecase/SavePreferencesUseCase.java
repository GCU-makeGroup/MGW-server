package com.awp.mgw.member.usecase;

import com.awp.mgw.member.controller.dto.request.SavePreferencesRequest;

public interface SavePreferencesUseCase {
    void savePreferences(Long memberId, SavePreferencesRequest request);
}
