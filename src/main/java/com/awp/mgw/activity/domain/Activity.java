package com.awp.mgw.activity.domain;

import com.awp.mgw.activity.domain.exception.ActivityDomainException;
import com.awp.mgw.activity.domain.exception.ActivityErrorCode;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "activity")
public class Activity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    /**
     * 기존 스키마 호환용 필드. 현재는 제목과 동일하게 관리합니다.
     */
    @Column(name = "subtitle", nullable = false, columnDefinition = "TEXT")
    private String subtitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Member creator;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_member", nullable = false)
    private Integer maxMember;

    @Column(name = "thumbnail_url", nullable = false, columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "schedule")
    private Instant schedule;

    @Column(name = "openchat_url", length = 255)
    private String openchatUrl;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityLike> activityLikes = new ArrayList<>();

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityGroup> activityGroups = new ArrayList<>();

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityCategory> activityCategories = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Activity(String title, String subtitle, Member creator, String description, Integer maxMember, String thumbnailUrl,
                     String location, Instant schedule, String openchatUrl, List<ActivityLike> activityLikes,
                     List<ActivityGroup> activityGroups, List<ActivityCategory> activityCategories) {
        this.title = title;
        this.subtitle = subtitle;
        this.creator = creator;
        this.description = description;
        this.maxMember = maxMember;
        this.thumbnailUrl = thumbnailUrl;
        this.location = location;
        this.schedule = schedule;
        this.openchatUrl = openchatUrl;
        this.activityLikes = activityLikes;
        this.activityGroups = activityGroups;
        this.activityCategories = activityCategories;
    }

    public static Activity create(String title, Member creator, String description, Integer maxMember,
                                  String thumbnailUrl, String location, Instant schedule, String openchatUrl) {
        validateTitle(title);
        validateCreator(creator);
        validateDescription(description);
        validateMaxMember(maxMember);
        validateThumbnailUrl(thumbnailUrl);
        validateLocation(location);
        validateSchedule(schedule);
        validateOpenchatUrl(openchatUrl);

        return Activity.builder()
            .title(title)
            .subtitle(title)
            .creator(creator)
            .description(description)
            .maxMember(maxMember)
            .thumbnailUrl(thumbnailUrl)
            .location(location)
            .schedule(schedule)
            .openchatUrl(openchatUrl)
            .activityLikes(new ArrayList<>())
            .activityGroups(new ArrayList<>())
            .activityCategories(new ArrayList<>())
            .build();
    }

    public void update(String title, String description, Integer maxMember, String thumbnailUrl,
                       String location, Instant schedule, String openchatUrl) {
        validateTitle(title);
        validateDescription(description);
        validateMaxMember(maxMember);
        validateThumbnailUrl(thumbnailUrl);
        validateLocation(location);
        validateSchedule(schedule);
        validateOpenchatUrl(openchatUrl);

        this.title = title;
        this.subtitle = title;
        this.description = description;
        this.maxMember = maxMember;
        this.thumbnailUrl = thumbnailUrl;
        this.location = location;
        this.schedule = schedule;
        this.openchatUrl = openchatUrl;
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new ActivityDomainException(ActivityErrorCode.INVALID_ACTIVITY_TITLE);
        }
    }

    private static void validateCreator(Member creator) {
        if (creator == null) {
            throw new ActivityDomainException(ActivityErrorCode.FORBIDDEN_ACTIVITY_ACCESS);
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new ActivityDomainException(ActivityErrorCode.INVALID_ACTIVITY_DESCRIPTION);
        }
    }

    private static void validateMaxMember(Integer maxMember) {
        if (maxMember == null || maxMember < 1) {
            throw new ActivityDomainException(ActivityErrorCode.INVALID_ACTIVITY_MAX_MEMBER);
        }
    }

    private static void validateThumbnailUrl(String thumbnailUrl) {
        if (thumbnailUrl == null || thumbnailUrl.isBlank()) {
            throw new ActivityDomainException(ActivityErrorCode.INVALID_ACTIVITY_THUMBNAIL_URL);
        }
    }

    private static void validateLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new ActivityDomainException(ActivityErrorCode.INVALID_ACTIVITY_LOCATION);
        }
    }

    private static void validateSchedule(Instant schedule) {
        if (schedule == null) {
            throw new ActivityDomainException(ActivityErrorCode.INVALID_ACTIVITY_SCHEDULE);
        }
    }

    private static void validateOpenchatUrl(String openchatUrl) {
        if (openchatUrl == null || openchatUrl.isBlank()) {
            throw new ActivityDomainException(ActivityErrorCode.INVALID_ACTIVITY_OPENCHAT_URL);
        }
    }
}
