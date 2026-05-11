package com.awp.mgw.member.controller.dto.response;

public record SignupResponse(
      Long memberId,
      String email,
      String name
) {
}