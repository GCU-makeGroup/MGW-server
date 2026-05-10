package com.awp.mgw.member.application;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationCodeGenerator {

  private static final int CODE_LENGTH = 6;
  private static final int DIGIT_BOUND = 10;

  private final SecureRandom secureRandom = new SecureRandom();

  public String generate() {
    StringBuilder code = new StringBuilder();

    for (int i = 0; i < CODE_LENGTH; i++) {
      code.append(secureRandom.nextInt(DIGIT_BOUND));
    }

    return code.toString();
  }
}