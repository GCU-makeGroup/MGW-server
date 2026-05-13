package com.awp.mgw.mypage.controller.dto.response;

public record MyPageProfileResponse(
      String name,
      String imageUrl,
      String introduction
) {
}