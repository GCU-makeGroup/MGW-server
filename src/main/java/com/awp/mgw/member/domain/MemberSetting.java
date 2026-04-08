package com.awp.mgw.member.domain;

import com.awp.mgw.common.BaseEntity;
import com.awp.mgw.member.domain.enums.Language;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_setting")
public class MemberSetting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Language language = Language.en;

    @Column(name = "dark_mode", nullable = false)
    private Boolean darkMode = false;

    @Column(name = "message_notification", nullable = false)
    private Boolean messageNotification = true;

    @Column(name = "group_invite_notification", nullable = false)
    private Boolean groupInviteNotification = true;

    @Column(name = "post_comment_notification", nullable = false)
    private Boolean postCommentNotification = true;

    @Builder(access = AccessLevel.PRIVATE)
    private MemberSetting(Member member, Language language, Boolean darkMode, Boolean messageNotification,
                          Boolean groupInviteNotification, Boolean postCommentNotification) {
        this.member = member;
        this.language = language;
        this.darkMode = darkMode;
        this.messageNotification = messageNotification;
        this.groupInviteNotification = groupInviteNotification;
        this.postCommentNotification = postCommentNotification;
    }

    public static MemberSetting create(Member member, Language language) {
        return MemberSetting.builder()
            .member(member)
            .language(language != null ? language : Language.en)
            .darkMode(false)
            .messageNotification(true)
            .groupInviteNotification(true)
            .postCommentNotification(true)
            .build();
    }
}
