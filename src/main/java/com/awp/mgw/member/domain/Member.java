package com.awp.mgw.member.domain;

import com.awp.mgw.activity.domain.ActivityLike;
import com.awp.mgw.common.BaseEntity;
import com.awp.mgw.group.domain.Comment;
import com.awp.mgw.group.domain.Group;
import com.awp.mgw.group.domain.GroupMember;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "point")
    private Integer point;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> groupMembers = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityLike> activityLikes = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberSetting> memberSettings = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Group> groups = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> comments = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Member(String email, String name, String imageUrl, String introduction, Integer point, Instant deletedAt,
                   List<GroupMember> groupMembers, List<ActivityLike> activityLikes, List<MemberSetting> memberSettings,
                   List<Group> groups, List<Comment> comments) {
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
        this.introduction = introduction;
        this.point = point;
        this.deletedAt = deletedAt;
        this.groupMembers = groupMembers;
        this.activityLikes = activityLikes;
        this.memberSettings = memberSettings;
        this.groups = groups;
        this.comments = comments;
    }

    public static Member create(String email, String name, String imageUrl, String introduction) {
        return Member.builder()
            .email(email)
            .name(name)
            .imageUrl(imageUrl)
            .introduction(introduction)
            .point(0)
            .groupMembers(new ArrayList<>())
            .activityLikes(new ArrayList<>())
            .memberSettings(new ArrayList<>())
            .groups(new ArrayList<>())
            .comments(new ArrayList<>())
            .build();
    }

    /**
     *  해당 회원의 모든 그룹 모집글, 댓글 참조 해제
      */
    public void detachRetainedReferences() {
        groups.forEach(Group::detachMember);
        comments.forEach(Comment::detachMember);
    }
}
