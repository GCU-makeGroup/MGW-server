package com.awp.mgw.member.controller;

import com.awp.mgw.member.controller.dto.request.LoginRequest;
import com.awp.mgw.member.controller.dto.request.SignupRequest;
import com.awp.mgw.member.controller.dto.response.LoginResponse;
import com.awp.mgw.member.controller.dto.response.SignupResponse;
import com.awp.mgw.member.usecase.LoginUseCase;
import com.awp.mgw.member.usecase.SignupUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final SignupUseCase signupUseCase;
  private final LoginUseCase loginUseCase;
  private final SendEmailVerificationUseCase sendEmailVerificationUseCase;
  private final VerifyEmailVerificationUseCase verifyEmailVerificationUseCase;
  private final ReissueTokenUseCase reissueTokenUseCase;

  @PostMapping("/signup")
  public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
    return signupUseCase.signup(request);
  }

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    return loginUseCase.login(request);
  }

  @PostMapping("/api/v1/auth/email-verification/send")
  public void sendEmailVerification(
        @Valid @RequestBody EmailVerificationSendRequest request
  ) {
    sendEmailVerificationUseCase.send(request.email());
  }

  @PostMapping("/api/v1/auth/email-verification/resend")
  public void resendEmailVerification(
        @Valid @RequestBody EmailVerificationSendRequest request
  ) {
    sendEmailVerificationUseCase.resend(request.email());
  }

  @PostMapping("/api/v1/auth/email-verification/verify")
  public void verifyEmailVerification(
        @Valid @RequestBody EmailVerificationVerifyRequest request
  ) {
    verifyEmailVerificationUseCase.verify(request.email(), request.code());
  }

  @PostMapping("/api/v1/auth/token/reissue")
  public TokenReissueResponse reissueAccessToken(
        @Valid @RequestBody TokenReissueRequest request
  ) {
    return reissueTokenUseCase.reissue(request.refreshToken());
  }
}