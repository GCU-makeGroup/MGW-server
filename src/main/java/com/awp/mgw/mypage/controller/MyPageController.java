package com.awp.mgw.mypage.controller;

import com.awp.mgw.mypage.controller.dto.response.MyPageMainResponse;
import com.awp.mgw.mypage.usecase.query.GetMyPageMainUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequiredArgsConstructor
public class MyPageController {

  private final GetMyPageMainUseCase getMyPageMainUseCase;

  @GetMapping("/api/v1/mypage")
  public MyPageMainResponse getMyPageMain(
        @AuthenticationPrincipal Long memberId,
        @RequestParam int year,
        @RequestParam int month
  ) {
    return getMyPageMainUseCase.getMyPageMain(
          memberId,
          YearMonth.of(year, month)
    );
  }
}