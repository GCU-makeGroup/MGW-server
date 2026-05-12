package com.awp.mgw.mypage.service;

import com.awp.mgw.activity.port.ActivityQueryRepository;
import com.awp.mgw.group.port.GroupMemberRepository;
import com.awp.mgw.member.domain.Member;
import com.awp.mgw.member.domain.exception.MemberDomainException;
import com.awp.mgw.member.domain.exception.MemberErrorCode;
import com.awp.mgw.member.port.MemberRepository;
import com.awp.mgw.mypage.controller.dto.response.MyPageCalendarResponse;
import com.awp.mgw.mypage.controller.dto.response.MyPageMainResponse;
import com.awp.mgw.mypage.controller.dto.response.MyPageProfileResponse;
import com.awp.mgw.mypage.controller.dto.response.MyPageSummaryResponse;
import com.awp.mgw.mypage.usecase.query.GetMyPageMainUseCase;
import com.awp.mgw.schedule.controller.dto.response.ScheduleDateResponse;
import com.awp.mgw.schedule.usecase.query.GetMonthlyScheduleUseCase;
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

  private static final int TEMP_POINT = 1250;

  private final MemberRepository memberRepository;
  private final ActivityQueryRepository activityQueryRepository;
  private final GroupMemberRepository groupMemberRepository;
  private final GetMonthlyScheduleUseCase getMonthlyScheduleUseCase;

  @Override
  public MyPageMainResponse getMyPageMain(Long memberId, YearMonth yearMonth) {
    Member member = memberRepository.findById(memberId)
          .orElseThrow(() -> new MemberDomainException(MemberErrorCode.MEMBER_NOT_FOUND));

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

    List<ScheduleDateResponse> schedules =
          getMonthlyScheduleUseCase.getMonthlySchedules(memberId, yearMonth);

    MyPageCalendarResponse calendar = new MyPageCalendarResponse(
          yearMonth.getYear(),
          yearMonth.getMonthValue(),
          LocalDate.now(),
          schedules
    );

    return new MyPageMainResponse(profile, summary, calendar);
  }
}