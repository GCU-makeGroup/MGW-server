package com.awp.mgw.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

  private static final String EMAIL_SUBJECT = "[MGW] 이메일 인증 코드";
  private static final String EMAIL_TEXT_FORMAT = "인증 코드는 [%s] 입니다. 5분 안에 입력해주세요.";

  private final JavaMailSender javaMailSender;

  @Override
  public void sendVerificationCode(String email, String code) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject(EMAIL_SUBJECT);
    message.setText(String.format(EMAIL_TEXT_FORMAT, code));

    javaMailSender.send(message);
  }
}