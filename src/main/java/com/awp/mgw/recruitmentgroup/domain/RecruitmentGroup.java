package com.awp.mgw.recruitmentgroup.domain;

import com.awp.mgw.common.BaseEntity;
import com.awp.mgw.group.domain.Group;
import com.awp.mgw.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recruitment_group")
public class RecruitmentGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = true)
    private Member member;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @OneToMany(mappedBy = "recruitmentGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private RecruitmentGroup(Group group, Member member, String title, String content, String imageUrl,
                             List<Comment> comments) {
        this.group = group;
        this.member = member;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.comments = comments;
    }

    public static RecruitmentGroup create(Group group, Member member, String title, String content, String imageUrl) {
        return RecruitmentGroup.builder()
            .group(group)
            .member(member)
            .title(title)
            .content(content)
            .imageUrl(imageUrl)
            .comments(new ArrayList<>())
            .build();
    }

    public void detachMember() {
        this.member = null;
    }
}
