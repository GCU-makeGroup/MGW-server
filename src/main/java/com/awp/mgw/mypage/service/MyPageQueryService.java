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
import com.awp.mgw.member.port.MemberRepository;
import com.awp.mgw.member.domain.Member;
import com.awp.mgw.activity.port.ActivityQueryRepository;
import com.awp.mgw.group.port.GroupMemberRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageQueryService implements GetMyPageMainUseCase {

  private static final int TEMP_POINT = 1250;

  private final MemberRepository memberRepository;
  private final ActivityQueryRepository activityQueryRepository;
  private final GroupMemberRepository groupMemberRepository;

  @Override
  public MyPageMainResponse getMyPageMain(Long memberId, YearMonth yearMonth) {
    Member member = memberRepository.findById(memberId)
          .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));    // TODO: 게시글 수 조회

    long activityCount = activityQueryRepository.countJoinedActivities(memberId);
    long groupCount = groupMemberRepository.countByMember_Id(memberId);

    MyPageProfileResponse profile = new MyPageProfileResponse(
          member.getName(),
          member.getImageUrl(),
          member.getIntroduction()
    );

    MyPageSummaryResponse summary = new MyPageSummaryResponse(
          activityCount,
          groupCount,
          TEMP_POINT
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