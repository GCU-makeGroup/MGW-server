package com.awp.mgw.notification.domain;

import com.awp.mgw.common.BaseEntity;
import com.awp.mgw.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private String type;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Builder(access = AccessLevel.PRIVATE)
    private Notification(Member member, String title, String message, String type, boolean isRead) {
        this.member = member;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
    }

    public static Notification create(Member member, String title, String message, String type) {
        return Notification.builder()
                .member(member)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
