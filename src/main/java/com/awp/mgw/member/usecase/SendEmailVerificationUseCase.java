package com.awp.mgw.member.usecase;

public interface SendEmailVerificationUseCase {

  void send(String email);

  void resend(String email);
}