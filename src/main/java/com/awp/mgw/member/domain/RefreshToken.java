package com.awp.mgw.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long memberId;

  private String token;

  private RefreshToken(Long memberId, String token) {
    this.memberId = memberId;
    this.token = token;
  }

  public static RefreshToken create(Long memberId, String token) {
    return new RefreshToken(memberId, token);
  }

  public void updateToken(String token) {
    this.token = token;
  }
}