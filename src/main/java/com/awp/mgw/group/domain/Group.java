package com.awp.mgw.group.domain;

import com.awp.mgw.common.BaseEntity;
import com.awp.mgw.group.domain.exception.GroupDomainException;
import com.awp.mgw.group.domain.exception.GroupErrorCode;
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
@Table(name = "`groups`")
public class Group extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Member member;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> groupMembers = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupCategory> groupCategories = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Group(String name, String title, String content, Member member, String imageUrl, Boolean isPublic, Integer capacity,
                  List<GroupMember> groupMembers, List<GroupCategory> groupCategories, List<Comment> comments) {
        this.name = name;
        this.title = title;
        this.content = content;
        this.member = member;
        this.imageUrl = imageUrl;
        this.isPublic = isPublic;
        this.capacity = capacity;
        this.groupMembers = groupMembers;
        this.groupCategories = groupCategories;
        this.comments = comments;
    }

    public static Group create(String name, String title, String content, Member member, String imageUrl, Boolean isPublic,
                               Integer capacity) {
        validateName(name);
        validateTitle(title);
        validateContent(content);
        validateImageUrl(imageUrl);
        validateCapacity(capacity);

        return Group.builder()
                .name(name)
                .title(title)
                .content(content)
                .member(member)
                .imageUrl(imageUrl)
                .isPublic(isPublic != null ? isPublic : false)
                .capacity(capacity)
                .groupMembers(new ArrayList<>())
                .groupCategories(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();
    }

    public void validateCapacityChange(Integer capacity) {
        validateCapacity(capacity);

        if (capacity < groupMembers.size()) {
            throw new GroupDomainException(GroupErrorCode.INVALID_GROUP_CAPACITY,
                "현재 그룹 인원보다 작은 수로 최대 인원을 변경할 수 없습니다.");
        }
    }

    public void updateGroup(String name, String title, String content, String imageUrl, Boolean isPublic, Integer capacity) {
        validateName(name);
        validateTitle(title);
        validateContent(content);
        validateImageUrl(imageUrl);
        validateCapacityChange(capacity);

        this.name = name;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.isPublic = isPublic;
        this.capacity = capacity;
    }

    public void detachMember() {
        this.member = null;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank() || name.length() > 50) {
            throw new GroupDomainException(GroupErrorCode.INVALID_GROUP_NAME);
        }
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new GroupDomainException(GroupErrorCode.INVALID_GROUP_TITLE);
        }
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new GroupDomainException(GroupErrorCode.INVALID_GROUP_CONTENT);
        }
    }

    private static void validateImageUrl(String imageUrl) {
        if (imageUrl != null && imageUrl.isBlank()) {
            throw new GroupDomainException(GroupErrorCode.INVALID_GROUP_IMAGE_URL);
        }
    }

    private static void validateCapacity(Integer capacity) {
        if (capacity == null || capacity < 1 || capacity > 20) {
            throw new GroupDomainException(GroupErrorCode.INVALID_GROUP_CAPACITY);
        }
    }
}
