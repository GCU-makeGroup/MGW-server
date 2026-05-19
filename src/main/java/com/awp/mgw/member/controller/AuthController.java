package com.awp.mgw.member.controller;

import com.awp.mgw.member.controller.dto.request.ChangePasswordRequest;
import com.awp.mgw.member.controller.dto.request.LoginRequest;
import com.awp.mgw.member.controller.dto.request.SavePreferencesRequest;
import com.awp.mgw.member.controller.dto.request.SignupRequest;
import com.awp.mgw.member.controller.dto.response.LoginResponse;
import com.awp.mgw.member.controller.dto.response.SignupResponse;
import com.awp.mgw.member.usecase.ChangePasswordUseCase;
import com.awp.mgw.member.usecase.LoginUseCase;
import com.awp.mgw.member.usecase.SavePreferencesUseCase;
import com.awp.mgw.member.usecase.SignupUseCase;
import com.awp.mgw.member.usecase.WithdrawMemberUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.awp.mgw.member.controller.dto.request.EmailVerificationSendRequest;
import com.awp.mgw.member.controller.dto.request.EmailVerificationVerifyRequest;
import com.awp.mgw.member.controller.dto.request.TokenReissueRequest;
import com.awp.mgw.member.controller.dto.response.TokenReissueResponse;
import com.awp.mgw.member.usecase.ReissueTokenUseCase;
import com.awp.mgw.member.usecase.SendEmailVerificationUseCase;
import com.awp.mgw.member.usecase.VerifyEmailVerificationUseCase;
import com.awp.mgw.member.usecase.LogoutUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final SignupUseCase signupUseCase;
  private final LoginUseCase loginUseCase;
  private final SendEmailVerificationUseCase sendEmailVerificationUseCase;
  private final VerifyEmailVerificationUseCase verifyEmailVerificationUseCase;
  private final ReissueTokenUseCase reissueTokenUseCase;
  private final LogoutUseCase logoutUseCase;
  private final ChangePasswordUseCase changePasswordUseCase;
  private final WithdrawMemberUseCase withdrawMemberUseCase;
  private final SavePreferencesUseCase savePreferencesUseCase;

  @PostMapping("/signup")
  public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
    return signupUseCase.signup(request);
  }

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    return loginUseCase.login(request);
  }

  @PostMapping("/logout")
  public void logout(@AuthenticationPrincipal Long memberId) {
    logoutUseCase.logout(memberId);
  }

  @PostMapping("/email-verification/send")
  public void sendEmailVerification(
        @Valid @RequestBody EmailVerificationSendRequest request
  ) {
    sendEmailVerificationUseCase.send(request.email());
  }

  @PostMapping("/email-verification/resend")
  public void resendEmailVerification(
        @Valid @RequestBody EmailVerificationSendRequest request
  ) {
    sendEmailVerificationUseCase.resend(request.email());
  }

  @PostMapping("/email-verification/verify")
  public void verifyEmailVerification(
        @Valid @RequestBody EmailVerificationVerifyRequest request
  ) {
    verifyEmailVerificationUseCase.verify(request.email(), request.code());
  }

  @PostMapping("/token/reissue")
  public TokenReissueResponse reissueAccessToken(
        @Valid @RequestBody TokenReissueRequest request
  ) {
    return reissueTokenUseCase.reissue(request.refreshToken());
  }

  @PatchMapping("/password")
  public void changePassword(
        @AuthenticationPrincipal Long memberId,
        @Valid @RequestBody ChangePasswordRequest request
  ) {
    changePasswordUseCase.changePassword(memberId, request);
  }

  @DeleteMapping("/withdraw")
  public void withdraw(@AuthenticationPrincipal Long memberId) {
    withdrawMemberUseCase.withdraw(memberId);
  }

  @PostMapping("/preferences")
  public void savePreferences(
        @AuthenticationPrincipal Long memberId,
        @RequestBody SavePreferencesRequest request
  ) {
    savePreferencesUseCase.savePreferences(memberId, request);
  }
}