package com.awp.mgw.activity.domain;

import com.awp.mgw.common.BaseEntity;
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
@Table(name = "activity")
public class Activity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "subtitle", nullable = false, columnDefinition = "TEXT")
    private String subtitle;

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
    private Activity(String title, String subtitle, String description, Integer maxMember, String thumbnailUrl,
                     String location, Instant schedule, String openchatUrl, List<ActivityLike> activityLikes,
                     List<ActivityGroup> activityGroups, List<ActivityCategory> activityCategories) {
        this.title = title;
        this.subtitle = subtitle;
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

    public static Activity create(String title, String subtitle, String description, Integer maxMember,
                                  String thumbnailUrl, String location, Instant schedule, String openchatUrl) {
        return Activity.builder()
            .title(title)
            .subtitle(subtitle)
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
}
