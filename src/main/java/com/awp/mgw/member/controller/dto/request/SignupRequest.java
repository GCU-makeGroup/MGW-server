package com.awp.mgw.member.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(

      @NotBlank(message = "이메일은 필수입니다.")
      @Email(message = "올바른 이메일 형식이 아닙니다.")
      @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@gachon\\.ac\\.kr$",
            message = "가천대학교 이메일만 가입할 수 있습니다."
      )
      String email,

      @NotBlank(message = "비밀번호는 필수입니다.")
      @Size(min = 8, max = 16, message = "비밀번호는 8자-16자 사이여야 합니다.")
      String password,

      @NotBlank(message = "이름은 필수입니다.")
      @Size(max = 20, message = "이름은 20자 이하로 입력해주세요.")
      String name,

      String imageUrl,

      String introduction
) {
}