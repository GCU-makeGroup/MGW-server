package com.awp.mgw.mypage.controller;

import com.awp.mgw.mypage.controller.dto.response.MyPageMainResponse;
import com.awp.mgw.mypage.usecase.query.GetMyPageMainUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.YearMonth;

@Validated
@RestController
@RequiredArgsConstructor
public class MyPageController {

  private final GetMyPageMainUseCase getMyPageMainUseCase;

  @GetMapping("/api/v1/mypage")
  public MyPageMainResponse getMyPageMain(
        @AuthenticationPrincipal Long memberId,

        @Min(value = 2000, message = "연도는 2000년 이상이어야 합니다.")
        @RequestParam int year,

        @Min(value = 1, message = "월은 1 이상이어야 합니다.")
        @Max(value = 12, message = "월은 12 이하여야 합니다.")
        @RequestParam int month,

        @RequestParam(required = false) LocalDate selectedDate
  ) {
    return getMyPageMainUseCase.getMyPageMain(
          memberId,
          YearMonth.of(year, month),
          selectedDate
    );
  }
}