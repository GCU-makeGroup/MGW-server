package com.awp.mgw.member.service;

import com.awp.mgw.member.controller.dto.request.ChangePasswordRequest;
import com.awp.mgw.member.domain.Member;
import com.awp.mgw.member.domain.exception.MemberDomainException;
import com.awp.mgw.member.domain.exception.MemberErrorCode;
import com.awp.mgw.member.port.MemberRepository;
import com.awp.mgw.member.port.RefreshTokenRepository;
import com.awp.mgw.member.usecase.ChangePasswordUseCase;
import com.awp.mgw.member.usecase.WithdrawMemberUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService implements ChangePasswordUseCase, WithdrawMemberUseCase {

    private final MemberRepository memberRepository;
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

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberDomainException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
