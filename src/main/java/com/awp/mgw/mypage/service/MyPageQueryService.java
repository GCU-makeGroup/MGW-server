package com.awp.mgw.mypage.service;

import com.awp.mgw.mypage.controller.dto.response.MyPageCalendarResponse;
import com.awp.mgw.mypage.controller.dto.response.MyPageMainResponse;
import com.awp.mgw.mypage.controller.dto.response.MyPageProfileResponse;
import com.awp.mgw.mypage.controller.dto.response.MyPageScheduleResponse;
import com.awp.mgw.mypage.controller.dto.response.MyPageSummaryResponse;
import com.awp.mgw.mypage.usecase.query.GetMyPageMainUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageQueryService implements GetMyPageMainUseCase {

  @Override
  public MyPageMainResponse getMyPageMain(Long memberId, YearMonth yearMonth) {
    // TODO: MemberRepository에서 회원 조회
    // TODO: 게시글 수 조회
    // TODO: 그룹 수 조회
    // TODO: 일정 조회

    MyPageProfileResponse profile = new MyPageProfileResponse(
          "Kim Min-jun",
          "Software Engineering",
          null,
          true,
          1250
    );

    MyPageSummaryResponse summary = new MyPageSummaryResponse(
          24,
          8,
          1250
    );

    List<MyPageScheduleResponse> schedules = List.of(
          new MyPageScheduleResponse(LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 3), true),
          new MyPageScheduleResponse(LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 6), true),
          new MyPageScheduleResponse(LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 11), true),
          new MyPageScheduleResponse(LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 21), true)
    );

    MyPageCalendarResponse calendar = new MyPageCalendarResponse(
          yearMonth.getYear(),
          yearMonth.getMonthValue(),
          LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 15),
          schedules
    );

    return new MyPageMainResponse(profile, summary, calendar);
  }
}