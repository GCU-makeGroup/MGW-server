package com.awp.mgw.mypage.controller.dto.response;

import com.awp.mgw.member.domain.Member;
import com.awp.mgw.member.domain.MemberSetting;
import com.awp.mgw.member.domain.enums.Language;

public record MyPageSettingsResponse(
        Profile profile,
        MatchingCommunication matchingCommunication,
        Notifications notifications,
        LanguageRegion languageRegion,
        AccountSecurity accountSecurity,
        System system
) {
    public static MyPageSettingsResponse from(Member member, MemberSetting setting) {
        return new MyPageSettingsResponse(
                new Profile(
                        member.getName(),
                        member.getImageUrl(),
                        setting != null ? setting.getLanguage() : Language.en,
                        setting != null ? setting.getDarkMode() : false,
                        setting != null ? setting.getMessageNotification() : true,
                        setting != null ? setting.getGroupInviteNotification() : true,
                        setting != null ? setting.getPostCommentNotification() : true
                ),
                new MatchingCommunication(
                        setting != null ? setting.getLanguage() : Language.en
                ),
                new Notifications(
                        setting != null ? setting.getMessageNotification() : true,
                        setting != null ? setting.getGroupInviteNotification() : true,
                        setting != null ? setting.getPostCommentNotification() : true
                ),
                new LanguageRegion(
                        setting != null ? setting.getLanguage() : Language.en
                ),
                new AccountSecurity(
                        member.getEmail()
                ),
                new System(
                        setting != null ? setting.getDarkMode() : false
                )
        );
    }

    public record Profile(String name, String profileImageUrl, Language language, Boolean darkMode,
                          Boolean messageNotification, Boolean groupInviteNotification, Boolean postCommentNotification) {}

    public record MatchingCommunication(Language preferredLanguage) {}

    public record Notifications(Boolean newMessages, Boolean groupInvites, Boolean postComments) {}

    public record LanguageRegion(Language appLanguage) {}

    public record AccountSecurity(String email) {}

    public record System(Boolean darkMode) {}
}
