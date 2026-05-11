package com.awp.mgw.mypage.controller.dto.response;

public record MyPageProfileResponse(
      String name,
      String department,
      String imageUrl,
      boolean academicVerified,
      int point
) {
}