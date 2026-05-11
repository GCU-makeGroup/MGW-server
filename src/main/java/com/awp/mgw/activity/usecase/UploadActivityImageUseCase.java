package com.awp.mgw.activity.usecase;

import com.awp.mgw.activity.controller.dto.response.ActivityImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UploadActivityImageUseCase {
    ActivityImageUploadResponse uploadActivityImage(Long memberId, MultipartFile file);
}
