package com.awp.mgw.member.service;

import com.awp.mgw.member.controller.dto.request.ChangePasswordRequest;
import com.awp.mgw.member.controller.dto.request.SavePreferencesRequest;
import com.awp.mgw.member.domain.Member;
import com.awp.mgw.member.domain.MemberSetting;
import com.awp.mgw.member.domain.exception.MemberDomainException;
import com.awp.mgw.member.domain.exception.MemberErrorCode;
import com.awp.mgw.member.port.MemberRepository;
import com.awp.mgw.member.port.MemberSettingRepository;
import com.awp.mgw.member.port.RefreshTokenRepository;
import com.awp.mgw.member.usecase.ChangePasswordUseCase;
import com.awp.mgw.member.usecase.SavePreferencesUseCase;
import com.awp.mgw.member.usecase.WithdrawMemberUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService implements ChangePasswordUseCase, WithdrawMemberUseCase, SavePreferencesUseCase {

    private final MemberRepository memberRepository;
    private final MemberSettingRepository memberSettingRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void changePassword(Long memberId, ChangePasswordRequest request) {
        Member member = getMemberOrThrow(memberId);

        if (!passwordEncoder.matches(request.currentPassword(), member.getPassword())) {
            throw new MemberDomainException(MemberErrorCode.INVALID_CURRENT_PASSWORD);
        }

        member.updatePassword(passwordEncoder.encode(request.newPassword()));
    }

    @Override
    public void withdraw(Long memberId) {
        Member member = getMemberOrThrow(memberId);

        member.detachRetainedReferences();
        member.softDelete();
        refreshTokenRepository.deleteByMemberId(memberId);
    }

    @Override
    public void savePreferences(Long memberId, SavePreferencesRequest request) {
        Member member = getMemberOrThrow(memberId);
        MemberSetting setting = memberSettingRepository.findByMember_Id(memberId)
                .orElseGet(() -> memberSettingRepository.save(
                        MemberSetting.create(member, null)));

        if (request.interestKeywords() != null) {
            setting.updateInterestKeywords(request.interestKeywords());
        }
        if (request.purpose() != null) {
            setting.updatePurpose(request.purpose());
        }
    }

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberDomainException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
