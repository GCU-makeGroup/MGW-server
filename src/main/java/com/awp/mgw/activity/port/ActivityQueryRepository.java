package com.awp.mgw.activity.port;

import com.awp.mgw.activity.domain.QActivity;
import com.awp.mgw.activity.domain.QActivityCategory;
import com.awp.mgw.activity.domain.QActivityGroup;
import com.awp.mgw.activity.domain.QActivityLike;
import com.awp.mgw.activity.domain.enums.ActivityGroupStatus;
import com.awp.mgw.category.domain.QCategory;
import com.awp.mgw.group.domain.Group;
import com.awp.mgw.group.domain.QGroup;
import com.awp.mgw.group.domain.QGroupMember;
import com.awp.mgw.member.domain.QMember;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ActivityQueryRepository {

    private static final QActivity activity = QActivity.activity;
    private static final QActivityCategory activityCategory = QActivityCategory.activityCategory;
    private static final QCategory category = QCategory.category;
    private static final QActivityLike activityLike = QActivityLike.activityLike;
    private static final QActivityGroup activityGroup = QActivityGroup.activityGroup;
    private static final QGroup group = QGroup.group;
    private static final QGroupMember groupMember = QGroupMember.groupMember;
    private static final QMember member = QMember.member;

    private final JPAQueryFactory queryFactory;

    public List<ActivitySummaryRow> findActivitySummaries(Long memberId, String categoryName, String scope, Long cursor, int size) {
        return queryFactory
            .select(Projections.constructor(
                ActivitySummaryRow.class,
                activity.id,
                activity.title,
                categoryNameExpression(),
                activity.maxMember,
                currentParticipantCountExpression(activity.id),
                isLikedExpression(memberId, activity.id),
                likeCountExpression(activity.id),
                activity.schedule,
                activity.thumbnailUrl,
                scoreExpression(activity.id)
            ))
            .from(activity)
            .where(
                cursorLt(cursor),
                categoryFilter(categoryName),
                scopeFilter(scope, memberId)
            )
            .orderBy(activity.id.desc())
            .limit(size)
            .fetch();
    }

    public ActivitySummaryRow findTopHotpick(Long memberId, String categoryName) {
        return queryFactory
            .select(Projections.constructor(
                ActivitySummaryRow.class,
                activity.id,
                activity.title,
                categoryNameExpression(),
                activity.maxMember,
                currentParticipantCountExpression(activity.id),
                isLikedExpression(memberId, activity.id),
                likeCountExpression(activity.id),
                activity.schedule,
                activity.thumbnailUrl,
                scoreExpression(activity.id)
            ))
            .from(activity)
            .where(categoryFilter(categoryName))
            .orderBy(scoreExpression(activity.id).desc(), activity.id.desc())
            .limit(1)
            .fetchOne();
    }

    public ActivitySummaryRow findActivitySummaryById(Long activityId, Long memberId) {
        return queryFactory
            .select(Projections.constructor(
                ActivitySummaryRow.class,
                activity.id,
                activity.title,
                categoryNameExpression(),
                activity.maxMember,
                currentParticipantCountExpression(activity.id),
                isLikedExpression(memberId, activity.id),
                likeCountExpression(activity.id),
                activity.schedule,
                activity.thumbnailUrl,
                scoreExpression(activity.id)
            ))
            .from(activity)
            .where(activity.id.eq(activityId))
            .fetchOne();
    }

    public List<ActivityParticipantRow> findParticipants(Long activityId) {
        return queryFactory
            .select(Projections.constructor(
                ActivityParticipantRow.class,
                member.id,
                member.name,
                member.imageUrl
            ))
            .from(activityGroup)
            .join(activityGroup.group, group)
            .join(group.groupMembers, groupMember)
            .join(groupMember.member, member)
            .where(
                activityGroup.activity.id.eq(activityId),
                activityGroup.status.eq(ActivityGroupStatus.JOIN)
            )
            .distinct()
            .fetch();
    }

    public long countJoinedParticipants(Long activityId) {
        Long count = queryFactory
            .select(groupMember.member.id.countDistinct())
            .from(activityGroup)
            .join(activityGroup.group, group)
            .join(group.groupMembers, groupMember)
            .where(
                activityGroup.activity.id.eq(activityId),
                activityGroup.status.eq(ActivityGroupStatus.JOIN)
            )
            .fetchOne();

        return count == null ? 0L : count;
    }

    public long countJoinedActivities(Long memberId) {
        Long count = queryFactory
              .select(activityGroup.activity.id.countDistinct())
              .from(activityGroup)
              .join(activityGroup.group, group)
              .join(group.groupMembers, groupMember)
              .where(
                    activityGroup.status.eq(ActivityGroupStatus.JOIN),
                    groupMember.member.id.eq(memberId)
              )
              .fetchOne();

        return count == null ? 0L : count;
    }

    public boolean existsJoinedMember(Long activityId, Long memberId) {
        Integer exists = queryFactory
            .selectOne()
            .from(activityGroup)
            .join(activityGroup.group, group)
            .join(group.groupMembers, groupMember)
            .where(
                activityGroup.activity.id.eq(activityId),
                activityGroup.status.eq(ActivityGroupStatus.JOIN),
                groupMember.member.id.eq(memberId)
            )
            .fetchFirst();

        return exists != null;
    }

    public boolean existsMemberInActivityByStatuses(Long activityId, Long memberId, Collection<ActivityGroupStatus> statuses) {
        Integer exists = queryFactory
            .selectOne()
            .from(activityGroup)
            .join(activityGroup.group, group)
            .join(group.groupMembers, groupMember)
            .where(
                activityGroup.activity.id.eq(activityId),
                activityGroup.status.in(statuses),
                groupMember.member.id.eq(memberId)
            )
            .fetchFirst();

        return exists != null;
    }

    public boolean existsMemberInGroup(Long groupId, Long memberId) {
        Integer exists = queryFactory
            .selectOne()
            .from(groupMember)
            .where(
                groupMember.group.id.eq(groupId),
                groupMember.member.id.eq(memberId)
            )
            .fetchFirst();

        return exists != null;
    }

    public Group findSingleMemberGroup(Long memberId) {
        QGroupMember memberCountAlias = new QGroupMember("memberCountAlias");
        QGroupMember memberExistsAlias = new QGroupMember("memberExistsAlias");

        return queryFactory
            .selectFrom(group)
            .where(
                group.member.id.eq(memberId),
                group.capacity.eq(1),
                group.isPublic.isFalse(),
                JPAExpressions.select(memberCountAlias.id.count())
                    .from(memberCountAlias)
                    .where(memberCountAlias.group.id.eq(group.id))
                    .eq(1L),
                JPAExpressions.selectOne()
                    .from(memberExistsAlias)
                    .where(
                        memberExistsAlias.group.id.eq(group.id),
                        memberExistsAlias.member.id.eq(memberId)
                    )
                    .exists()
            )
            .orderBy(group.id.desc())
            .fetchFirst();
    }

    public long countGroupMembers(Long groupId) {
        Long count = queryFactory
            .select(groupMember.member.id.countDistinct())
            .from(groupMember)
            .where(groupMember.group.id.eq(groupId))
            .fetchOne();

        return count == null ? 0L : count;
    }

    public long countAlreadyJoinedMembersFromGroup(Long activityId, Long groupId) {
        QGroupMember joinTargetMembers = new QGroupMember("joinTargetMembers");

        Long count = queryFactory
            .select(groupMember.member.id.countDistinct())
            .from(activityGroup)
            .join(activityGroup.group, group)
            .join(group.groupMembers, groupMember)
            .where(
                activityGroup.activity.id.eq(activityId),
                activityGroup.status.eq(ActivityGroupStatus.JOIN),
                groupMember.member.id.in(
                    JPAExpressions.select(joinTargetMembers.member.id)
                        .from(joinTargetMembers)
                        .where(joinTargetMembers.group.id.eq(groupId))
                )
            )
            .fetchOne();

        return count == null ? 0L : count;
    }

    private BooleanExpression cursorLt(Long cursor) {
        if (cursor == null) {
            return null;
        }
        return activity.id.lt(cursor);
    }

    private BooleanExpression categoryFilter(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return null;
        }

        return JPAExpressions.selectOne()
            .from(activityCategory)
            .join(activityCategory.category, category)
            .where(
                activityCategory.activity.id.eq(activity.id),
                category.name.eq(categoryName)
            )
            .exists();
    }

    private BooleanExpression scopeFilter(String scope, Long memberId) {
        if (scope == null || scope.isBlank() || "hotpick".equalsIgnoreCase(scope)) {
            return null;
        }

        if ("created".equalsIgnoreCase(scope)) {
            return activity.creator.id.eq(memberId);
        }

        if ("joined".equalsIgnoreCase(scope)) {
            return JPAExpressions.selectOne()
                .from(activityGroup)
                .join(activityGroup.group, group)
                .join(group.groupMembers, groupMember)
                .where(
                    activityGroup.activity.id.eq(activity.id),
                    activityGroup.status.eq(ActivityGroupStatus.JOIN),
                    groupMember.member.id.eq(memberId)
                )
                .exists();
        }

        return null;
    }

    private Expression<String> categoryNameExpression() {
        QActivityCategory categoryPick = new QActivityCategory("categoryPick");
        QActivityCategory categoryMin = new QActivityCategory("categoryMin");
        QCategory categoryPickCategory = new QCategory("categoryPickCategory");

        return JPAExpressions.select(categoryPickCategory.name)
            .from(categoryPick)
            .join(categoryPick.category, categoryPickCategory)
            .where(
                categoryPick.id.eq(
                    JPAExpressions.select(categoryMin.id.min())
                        .from(categoryMin)
                        .where(categoryMin.activity.id.eq(activity.id))
                )
            );
    }

    private NumberExpression<Long> currentParticipantCountExpression(Expression<Long> activityIdExpression) {
        return Expressions.numberTemplate(
            Long.class,
            "coalesce(({0}), 0)",
            JPAExpressions.select(groupMember.member.id.countDistinct())
                .from(activityGroup)
                .join(activityGroup.group, group)
                .join(group.groupMembers, groupMember)
                .where(
                    activityGroup.activity.id.eq(activityIdExpression),
                    activityGroup.status.eq(ActivityGroupStatus.JOIN)
                )
        );
    }

    private NumberExpression<Long> likeCountExpression(Expression<Long> activityIdExpression) {
        return Expressions.numberTemplate(
            Long.class,
            "coalesce(({0}), 0)",
            JPAExpressions.select(activityLike.id.count())
                .from(activityLike)
                .where(activityLike.activity.id.eq(activityIdExpression))
        );
    }

    private Expression<Boolean> isLikedExpression(Long memberId, Expression<Long> activityIdExpression) {
        if (memberId == null) {
            return Expressions.asBoolean(false);
        }

        return JPAExpressions.selectOne()
            .from(activityLike)
            .where(
                activityLike.activity.id.eq(activityIdExpression),
                activityLike.member.id.eq(memberId)
            )
            .exists();
    }

    private NumberExpression<Long> scoreExpression(Expression<Long> activityIdExpression) {
        return Expressions.numberTemplate(
            Long.class,
            "(({0}) * 2 + ({1}))",
            likeCountExpression(activityIdExpression),
            currentParticipantCountExpression(activityIdExpression)
        );
    }

    public record ActivitySummaryRow(
        Long id,
        String title,
        String category,
        Integer capacity,
        Long currentParticipants,
        Boolean isLiked,
        Long likeCount,
        Instant schedule,
        String thumbnail,
        Long score
    ) {
    }

    public record ActivityParticipantRow(
        Long userId,
        String name,
        String profileImg
    ) {
    }

    // schedule에 사용될 activity 일정
    public List<LocalDate> findMonthlyScheduleDates(Long memberId, Instant start, Instant end) {
        return queryFactory
              .select(activity.schedule)
              .from(activityGroup)
              .join(activityGroup.activity, activity)
              .join(activityGroup.group, group)
              .join(group.groupMembers, groupMember)
              .where(
                    activityGroup.status.eq(ActivityGroupStatus.JOIN),
                    groupMember.member.id.eq(memberId),
                    activity.schedule.goe(start),
                    activity.schedule.lt(end)
              )
              .distinct()
              .fetch()
              .stream()
              .map(instant -> instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate())
              .toList();
    }
}
