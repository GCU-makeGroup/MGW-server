package com.awp.mgw.notification.controller;

import com.awp.mgw.common.exception.CommonException;
import com.awp.mgw.global.exception.constant.CommonErrorCode;
import com.awp.mgw.notification.controller.dto.response.NotificationResponse;
import com.awp.mgw.notification.domain.Notification;
import com.awp.mgw.notification.port.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping("/api/v1/notifications")
    public List<NotificationResponse> getNotifications(@AuthenticationPrincipal Long memberId) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @PatchMapping("/api/v1/notifications/{id}/read")
    public void markAsRead(@AuthenticationPrincipal Long memberId, @PathVariable Long id) {
        Notification notification = notificationRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "알림을 찾을 수 없습니다."));
        notification.markAsRead();
        notificationRepository.save(notification);
    }
}
