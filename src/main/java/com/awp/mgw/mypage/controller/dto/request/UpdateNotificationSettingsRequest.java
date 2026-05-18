package com.awp.mgw.mypage.controller.dto.request;

public record UpdateNotificationSettingsRequest(
        Boolean messageNotification,
        Boolean groupInviteNotification,
        Boolean postCommentNotification
) {
}
