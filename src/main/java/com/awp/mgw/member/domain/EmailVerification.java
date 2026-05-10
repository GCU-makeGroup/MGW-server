package com.awp.mgw.member.domain;

import com.awp.mgw.member.domain.exception.MemberDomainException;
import com.awp.mgw.member.domain.exception.MemberErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerification {

  private static final int CODE_EXPIRE_MINUTES = 5;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String code;

  @Column(nullable = false)
  private LocalDateTime expiredAt;

  @Column(nullable = false)
  private boolean verified;

  private EmailVerification(String email, String code, LocalDateTime expiredAt) {
    this.email = email;
    this.code = code;
    this.expiredAt = expiredAt;
    this.verified = false;
  }

  public static EmailVerification create(String email, String code) {
    return new EmailVerification(
          email,
          code,
          LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES)
    );
  }

  public void resend(String code) {
    if (this.verified) {
      throw new MemberDomainException(MemberErrorCode.EMAIL_ALREADY_VERIFIED);
    }

    this.code = code;
    this.expiredAt = LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES);
  }

  public void verify(String code) {
    if (this.verified) {
      throw new MemberDomainException(MemberErrorCode.EMAIL_ALREADY_VERIFIED);
    }

    if (LocalDateTime.now().isAfter(this.expiredAt)) {
      throw new MemberDomainException(MemberErrorCode.EMAIL_VERIFICATION_CODE_EXPIRED);
    }

    if (!this.code.equals(code)) {
      throw new MemberDomainException(MemberErrorCode.EMAIL_VERIFICATION_CODE_INVALID);
    }

    this.verified = true;
  }
}