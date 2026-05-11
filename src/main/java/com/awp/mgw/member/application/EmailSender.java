package com.awp.mgw.member.application;

public interface EmailSender {

  void sendVerificationCode(String email, String code);
}