package com.awp.mgw.mypage.usecase.query;

import com.awp.mgw.mypage.controller.dto.response.MyPageMainResponse;

import java.time.YearMonth;

public interface GetMyPageMainUseCase {
  MyPageMainResponse getMyPageMain(Long memberId, YearMonth yearMonth);
}