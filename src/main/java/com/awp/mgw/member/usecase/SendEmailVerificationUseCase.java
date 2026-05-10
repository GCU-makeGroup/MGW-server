package com.awp.mgw.member.usecase.command;

public interface SendEmailVerificationUseCase {

  void send(String email);

  void resend(String email);
}