package com.awp.mgw.member.controller.dto.response;

public record LoginResponse(
      String accessToken,
      String refreshToken,
      Long memberId,
      String email,
      String name
) {
}