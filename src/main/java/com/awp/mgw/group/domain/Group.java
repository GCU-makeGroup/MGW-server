package com.awp.mgw.group.domain;

import com.awp.mgw.common.BaseEntity;
import com.awp.mgw.recruitmentgroup.domain.RecruitmentGroup;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "`groups`")
public class Group extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> groupMembers = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupCategory> groupCategories = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitmentGroup> recruitmentGroups = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Group(String title, String content, Boolean isPublic, List<GroupMember> groupMembers,
                  List<GroupCategory> groupCategories, List<RecruitmentGroup> recruitmentGroups) {
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.groupMembers = groupMembers;
        this.groupCategories = groupCategories;
        this.recruitmentGroups = recruitmentGroups;
    }

    public static Group create(String title, String content, Boolean isPublic) {
        return Group.builder()
            .title(title)
            .content(content)
            .isPublic(isPublic != null ? isPublic : false)
            .groupMembers(new ArrayList<>())
            .groupCategories(new ArrayList<>())
            .recruitmentGroups(new ArrayList<>())
            .build();
    }
}
