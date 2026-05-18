package com.awp.mgw.mypage.service;

import com.awp.mgw.member.domain.Member;
import com.awp.mgw.member.domain.MemberSetting;
import com.awp.mgw.member.domain.enums.Language;
import com.awp.mgw.member.domain.exception.MemberDomainException;
import com.awp.mgw.member.domain.exception.MemberErrorCode;
import com.awp.mgw.member.port.MemberRepository;
import com.awp.mgw.member.port.MemberSettingRepository;
import com.awp.mgw.mypage.controller.dto.request.*;
import com.awp.mgw.mypage.controller.dto.response.MyPageSettingsResponse;
import com.awp.mgw.mypage.usecase.command.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageCommandService implements
        UpdateMyPageProfileUseCase,
        GetMyPageSettingsUseCase,
        UpdateMatchingCommunicationUseCase,
        UpdateNotificationSettingsUseCase,
        UpdateAppLanguageUseCase,
        UpdateDarkModeUseCase {

    private final MemberRepository memberRepository;
    private final MemberSettingRepository memberSettingRepository;

    @Override
    public void updateProfile(Long memberId, UpdateProfileRequest request) {
        Member member = getMemberOrThrow(memberId);
        member.updateProfile(request.name(), request.profileImageUrl());
    }

    @Override
    @Transactional(readOnly = true)
    public MyPageSettingsResponse getSettings(Long memberId) {
        Member member = getMemberOrThrow(memberId);
        MemberSetting setting = memberSettingRepository.findByMember_Id(memberId).orElse(null);
        return MyPageSettingsResponse.from(member, setting);
    }

    @Override
    public void updateMatchingCommunication(Long memberId, UpdateMatchingCommunicationRequest request) {
        MemberSetting setting = getOrCreateSetting(memberId);
        if (request.preferredLanguage() != null) {
            setting.updateLanguage(request.preferredLanguage());
        }
        // TODO: interestKeywords 저장 — MemberSetting에 interestKeywords 필드 추가 후 구현
    }

    @Override
    public void updateNotificationSettings(Long memberId, UpdateNotificationSettingsRequest request) {
        MemberSetting setting = getOrCreateSetting(memberId);
        setting.updateNotifications(
                request.messageNotification(),
                request.groupInviteNotification(),
                request.postCommentNotification()
        );
    }

    @Override
    public void updateAppLanguage(Long memberId, UpdateAppLanguageRequest request) {
        MemberSetting setting = getOrCreateSetting(memberId);
        setting.updateLanguage(request.appLanguage());
    }

    @Override
    public void updateDarkMode(Long memberId, UpdateDarkModeRequest request) {
        MemberSetting setting = getOrCreateSetting(memberId);
        setting.updateDarkMode(request.darkMode());
    }

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberDomainException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private MemberSetting getOrCreateSetting(Long memberId) {
        Member member = getMemberOrThrow(memberId);
        return memberSettingRepository.findByMember_Id(memberId)
                .orElseGet(() -> {
                    MemberSetting newSetting = MemberSetting.create(member, Language.en);
                    return memberSettingRepository.save(newSetting);
                });
    }
}
