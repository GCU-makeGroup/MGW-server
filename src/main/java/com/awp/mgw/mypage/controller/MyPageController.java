package com.awp.mgw.mypage.controller;

import com.awp.mgw.mypage.controller.dto.request.*;
import com.awp.mgw.mypage.controller.dto.response.MyPageMainResponse;
import com.awp.mgw.mypage.controller.dto.response.MyPageSettingsResponse;
import com.awp.mgw.mypage.controller.dto.response.ProfileImageUploadResponse;
import com.awp.mgw.mypage.usecase.command.*;
import com.awp.mgw.mypage.usecase.query.GetMyPageMainUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
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
  private final UpdateMyPageProfileUseCase updateMyPageProfileUseCase;
  private final UploadProfileImageUseCase uploadProfileImageUseCase;
  private final GetMyPageSettingsUseCase getMyPageSettingsUseCase;
  private final UpdateMatchingCommunicationUseCase updateMatchingCommunicationUseCase;
  private final UpdateNotificationSettingsUseCase updateNotificationSettingsUseCase;
  private final UpdateAppLanguageUseCase updateAppLanguageUseCase;
  private final UpdateDarkModeUseCase updateDarkModeUseCase;

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
    return getMyPageMainUseCase.getMyPageMain(memberId, YearMonth.of(year, month), selectedDate);
  }

  @PatchMapping("/api/v1/mypage/profile")
  public void updateProfile(
        @AuthenticationPrincipal Long memberId,
        @Valid @RequestBody UpdateProfileRequest request
  ) {
    updateMyPageProfileUseCase.updateProfile(memberId, request);
  }

  @PostMapping(value = "/api/v1/mypage/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ProfileImageUploadResponse uploadProfileImage(
        @AuthenticationPrincipal Long memberId,
        @RequestPart("file") MultipartFile file
  ) {
    return uploadProfileImageUseCase.uploadProfileImage(memberId, file);
  }

  @GetMapping("/api/v1/mypage/settings")
  public MyPageSettingsResponse getSettings(@AuthenticationPrincipal Long memberId) {
    return getMyPageSettingsUseCase.getSettings(memberId);
  }

  @PatchMapping("/api/v1/mypage/settings/matching-communication")
  public void updateMatchingCommunication(
        @AuthenticationPrincipal Long memberId,
        @Valid @RequestBody UpdateMatchingCommunicationRequest request
  ) {
    updateMatchingCommunicationUseCase.updateMatchingCommunication(memberId, request);
  }

  @PatchMapping("/api/v1/mypage/settings/notifications")
  public void updateNotificationSettings(
        @AuthenticationPrincipal Long memberId,
        @Valid @RequestBody UpdateNotificationSettingsRequest request
  ) {
    updateNotificationSettingsUseCase.updateNotificationSettings(memberId, request);
  }

  @PatchMapping("/api/v1/mypage/settings/app-language")
  public void updateAppLanguage(
        @AuthenticationPrincipal Long memberId,
        @Valid @RequestBody UpdateAppLanguageRequest request
  ) {
    updateAppLanguageUseCase.updateAppLanguage(memberId, request);
  }

  @PatchMapping("/api/v1/mypage/settings/dark-mode")
  public void updateDarkMode(
        @AuthenticationPrincipal Long memberId,
        @Valid @RequestBody UpdateDarkModeRequest request
  ) {
    updateDarkModeUseCase.updateDarkMode(memberId, request);
  }
}
