package com.awp.mgw.member.usecase;

public interface VerifyEmailVerificationUseCase {

  void verify(String email, String code);
}