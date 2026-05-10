package com.awp.mgw.member.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenReissueRequest(

      @NotBlank(message = "Refresh Token은 필수입니다.")
      String refreshToken
) {
}