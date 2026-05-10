package com.awp.mgw.member.usecase.command;

public interface VerifyEmailVerificationUseCase {

  void verify(String email, String code);
}