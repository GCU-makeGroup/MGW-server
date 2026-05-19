package com.awp.mgw.mypage.usecase.command;

import com.awp.mgw.mypage.controller.dto.response.ProfileImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UploadProfileImageUseCase {
    ProfileImageUploadResponse uploadProfileImage(Long memberId, MultipartFile file);
}
