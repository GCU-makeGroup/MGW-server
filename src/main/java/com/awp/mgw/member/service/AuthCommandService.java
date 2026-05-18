package com.awp.mgw.member.service;

import com.awp.mgw.member.controller.dto.request.LoginRequest;
import com.awp.mgw.member.controller.dto.request.SignupRequest;
import com.awp.mgw.member.controller.dto.response.LoginResponse;
import com.awp.mgw.member.controller.dto.response.SignupResponse;
import com.awp.mgw.member.domain.Member;
import com.awp.mgw.member.port.MemberRepository;
import com.awp.mgw.member.usecase.LoginUseCase;
import com.awp.mgw.member.usecase.LogoutUseCase;
import com.awp.mgw.member.usecase.SignupUseCase;
import com.awp.mgw.member.domain.exception.MemberDomainException;
import com.awp.mgw.member.domain.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.awp.mgw.global.security.jwt.JwtTokenProvider;
import org.springframework.dao.DataIntegrityViolationException;
import com.awp.mgw.member.application.EmailSender;
import com.awp.mgw.member.application.EmailVerificationCodeGenerator;
import com.awp.mgw.member.controller.dto.response.TokenReissueResponse;
import com.awp.mgw.member.domain.EmailVerification;
import com.awp.mgw.member.port.EmailVerificationRepository;
import com.awp.mgw.member.usecase.ReissueTokenUseCase;
import com.awp.mgw.member.usecase.SendEmailVerificationUseCase;
import com.awp.mgw.member.usecase.VerifyEmailVerificationUseCase;
import com.awp.mgw.member.port.RefreshTokenRepository;
import com.awp.mgw.member.domain.RefreshToken;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthCommandService implements SignupUseCase, LoginUseCase, LogoutUseCase,
      SendEmailVerificationUseCase,
      VerifyEmailVerificationUseCase,
      ReissueTokenUseCase {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final EmailVerificationRepository emailVerificationRepository;
  private final EmailVerificationCodeGenerator codeGenerator;
  private final EmailSender emailSender;
  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  @Transactional
  public SignupResponse signup(SignupRequest request) {
    if (!emailVerificationRepository.existsByEmailAndVerifiedTrue(request.email())) {
      throw new MemberDomainException(MemberErrorCode.EMAIL_NOT_VERIFIED);
    }

    if (memberRepository.existsByEmail(request.email())) {
      throw new MemberDomainException(MemberErrorCode.DUPLICATE_MEMBER_EMAIL);
    }

    String encodedPassword = passwordEncoder.encode(request.password());

    Member member = Member.create(
          request.email(),
          encodedPassword,
          request.name(),
          request.imageUrl(),
          request.introduction()
    );

    try {
      Member savedMember = memberRepository.save(member);

      return new SignupResponse(
            savedMember.getId(),
            savedMember.getEmail(),
            savedMember.getName()
      );
    } catch (DataIntegrityViolationException e) {
      throw new MemberDomainException(MemberErrorCode.DUPLICATE_MEMBER_EMAIL);
    }
  }

  @Override
  @Transactional
  public LoginResponse login(LoginRequest request) {
    Member member = memberRepository.findByEmail(request.email())
          .orElseThrow(() ->
                new MemberDomainException(MemberErrorCode.INVALID_LOGIN_INFO));

    if (!passwordEncoder.matches(request.password(), member.getPassword())) {
      throw new MemberDomainException(MemberErrorCode.INVALID_LOGIN_INFO);
    }

    if (member.getDeletedAt() != null) {
      throw new MemberDomainException(MemberErrorCode.MEMBER_WITHDRAWN);
    }

    String accessToken = jwtTokenProvider.createAccessToken(
          member.getId(),
          member.getEmail()
    );

    String refreshToken = jwtTokenProvider.createRefreshToken(
          member.getId(),
          member.getEmail()
    );

    refreshTokenRepository.findByMemberId(member.getId())
          .ifPresentOrElse(
                savedToken -> savedToken.updateToken(refreshToken),
                () -> refreshTokenRepository.save(
                      RefreshToken.create(member.getId(), refreshToken)
                )
          );

    return new LoginResponse(
          accessToken,
          refreshToken,
          member.getId(),
          member.getEmail(),
          member.getName()
    );
  }

  @Override
  @Transactional
  public void logout(Long memberId) {
    refreshTokenRepository.deleteByMemberId(memberId);
  }

  @Override
  @Transactional
  public void send(String email) {
    validateNotRegisteredEmail(email);

    String code = codeGenerator.generate();

    EmailVerification emailVerification = emailVerificationRepository.findByEmail(email)
          .map(existingVerification -> {
            existingVerification.resend(code);
            return existingVerification;
          })
          .orElseGet(() -> EmailVerification.create(email, code));

    emailVerificationRepository.save(emailVerification);
    emailSender.sendVerificationCode(email, code);
  }

  @Override
  @Transactional
  public void resend(String email) {
    validateNotRegisteredEmail(email);

    EmailVerification emailVerification = emailVerificationRepository.findByEmail(email)
          .orElseThrow(() ->
                new MemberDomainException(MemberErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

    String code = codeGenerator.generate();
    emailVerification.resend(code);

    emailSender.sendVerificationCode(email, code);
  }

  @Override
  @Transactional
  public void verify(String email, String code) {
    EmailVerification emailVerification = emailVerificationRepository.findByEmail(email)
          .orElseThrow(() ->
                new MemberDomainException(MemberErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

    emailVerification.verify(code);
  }

  @Override
  @Transactional
  public TokenReissueResponse reissue(String refreshToken) {
    if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
      throw new MemberDomainException(MemberErrorCode.INVALID_REFRESH_TOKEN);
    }

    Long memberId = jwtTokenProvider.getMemberId(refreshToken);

    Member member = memberRepository.findById(memberId)
          .orElseThrow(() -> new MemberDomainException(MemberErrorCode.INVALID_REFRESH_TOKEN));

    if (member.getDeletedAt() != null) {
      throw new MemberDomainException(MemberErrorCode.MEMBER_WITHDRAWN);
    }

    RefreshToken savedToken = refreshTokenRepository.findByMemberId(memberId)
          .orElseThrow(() -> new MemberDomainException(MemberErrorCode.INVALID_REFRESH_TOKEN));

    if (!savedToken.getToken().equals(refreshToken)) {
      throw new MemberDomainException(MemberErrorCode.INVALID_REFRESH_TOKEN);
    }

    String email = jwtTokenProvider.getEmail(refreshToken);

    return issueTokens(memberId, email);
  }

  private TokenReissueResponse issueTokens(Long memberId, String email) {
    String accessToken = jwtTokenProvider.createAccessToken(memberId, email);
    String refreshToken = jwtTokenProvider.createRefreshToken(memberId, email);

    refreshTokenRepository.findByMemberId(memberId)
          .ifPresentOrElse(
                savedToken -> savedToken.updateToken(refreshToken),
                () -> refreshTokenRepository.save(
                      RefreshToken.create(memberId, refreshToken)
                )
          );

    return new TokenReissueResponse(accessToken, refreshToken);
  }

  private void validateNotRegisteredEmail(String email) {
    if (memberRepository.existsByEmail(email)) {
      throw new MemberDomainException(MemberErrorCode.DUPLICATE_MEMBER_EMAIL);
    }
  }
}