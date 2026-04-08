package com.awp.mgw.recruitmentgroup.domain;

import com.awp.mgw.common.BaseEntity;
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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment")
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_group_id")
    private RecruitmentGroup recruitmentGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Comment(RecruitmentGroup recruitmentGroup, Member member, Comment parent, String content,
                    List<Comment> children) {
        this.recruitmentGroup = recruitmentGroup;
        this.member = member;
        this.parent = parent;
        this.content = content;
        this.children = children;
    }

    public static Comment create(RecruitmentGroup recruitmentGroup, Member member, Comment parent, String content) {
        return Comment.builder()
            .recruitmentGroup(recruitmentGroup)
            .member(member)
            .parent(parent)
            .content(content)
            .children(new ArrayList<>())
            .build();
    }

    public void detachMember() {
        this.member = null;
    }
}
