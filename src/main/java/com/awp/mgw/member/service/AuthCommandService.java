package com.awp.mgw.member.service;

import com.awp.mgw.member.controller.dto.request.LoginRequest;
import com.awp.mgw.member.controller.dto.request.SignupRequest;
import com.awp.mgw.member.controller.dto.response.LoginResponse;
import com.awp.mgw.member.controller.dto.response.SignupResponse;
import com.awp.mgw.member.domain.Member;
import com.awp.mgw.member.port.MemberRepository;
import com.awp.mgw.member.usecase.LoginUseCase;
import com.awp.mgw.member.usecase.SignupUseCase;
import com.awp.mgw.member.domain.exception.MemberDomainException;
import com.awp.mgw.member.domain.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.awp.mgw.global.security.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthCommandService implements SignupUseCase, LoginUseCase {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  @Transactional
  public SignupResponse signup(SignupRequest request) {
    if (memberRepository.existsByEmail(request.email())) {
      throw new MemberDomainException(MemberErrorCode.DUPLICATE_MEMBER_EMAIL);    }

    String encodedPassword = passwordEncoder.encode(request.password());

    Member member = Member.create(
          request.email(),
          encodedPassword,
          request.name(),
          request.imageUrl(),
          request.introduction()
    );

    Member savedMember = memberRepository.save(member);

    return new SignupResponse(
          savedMember.getId(),
          savedMember.getEmail(),
          savedMember.getName()
    );
  }

  @Override
  public LoginResponse login(LoginRequest request) {
    Member member = memberRepository.findByEmail(request.email())
          .orElseThrow(() ->
                new MemberDomainException(MemberErrorCode.INVALID_LOGIN_INFO));

    if (!passwordEncoder.matches(request.password(), member.getPassword())) {
      throw new MemberDomainException(MemberErrorCode.INVALID_LOGIN_INFO);
    }

    String accessToken = jwtTokenProvider.createAccessToken(
          member.getId(),
          member.getEmail()
    );

    String refreshToken = jwtTokenProvider.createRefreshToken(
          member.getId(),
          member.getEmail()
    );

    return new LoginResponse(
          accessToken,
          refreshToken,
          member.getId(),
          member.getEmail(),
          member.getName()
    );
  }
}