package com.awp.mgw.activity.domain;

import com.awp.mgw.common.BaseEntity;
import com.awp.mgw.activity.domain.enums.ActivityGroupStatus;
import com.awp.mgw.group.domain.Group;
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
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "activity_group",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_activity_group_activity_group_status",
            columnNames = {"activity_id", "group_id", "status"}
        )
    }
)
public class ActivityGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ActivityGroupStatus status;

    @Builder(access = AccessLevel.PRIVATE)
    private ActivityGroup(Activity activity, Group group, ActivityGroupStatus status) {
        this.activity = activity;
        this.group = group;
        this.status = status;
    }

    public static ActivityGroup create(Activity activity, Group group, ActivityGroupStatus status) {
        return ActivityGroup.builder()
            .activity(activity)
            .group(group)
            .status(status)
            .build();
    }
}
