package com.awp.mgw.group.port;

import com.awp.mgw.category.domain.Category;
import com.awp.mgw.group.domain.Comment;
import com.awp.mgw.group.domain.Group;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.awp.mgw.category.domain.QCategory.category;
import static com.awp.mgw.group.domain.QComment.comment;
import static com.awp.mgw.group.domain.QGroup.group;
import static com.awp.mgw.group.domain.QGroupCategory.groupCategory;
import static com.awp.mgw.group.domain.QGroupMember.groupMember;
import static com.awp.mgw.member.domain.QMember.member;

/**
 * 동적 쿼리를 위한 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class GroupQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Group> findGroupList(Long memberId, List<Long> categoryIds, Pageable pageable) {
        OrderSpecifier<?>[] orderSpecifiers = toOrderSpecifiers(pageable.getSort());

        // 컬렉션 조인을 바로 페이징하지 않기 위해, 먼저 조건에 맞는 그룹 ID만 페이지 단위로 조회
        List<Long> groupIds = queryFactory
                .select(group.id)
                .from(group)
                .leftJoin(group.groupCategories, groupCategory)
                .where(accessibleGroup(memberId), categoryIdIn(categoryIds))
                .groupBy(group.id)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 필터 조건과 동일한 기준으로 전체 개수를 따로 구해 Page 정보를 만들기
        Long total = queryFactory
                .select(group.id.countDistinct())
                .from(group)
                .leftJoin(group.groupCategories, groupCategory)
                .where(accessibleGroup(memberId), categoryIdIn(categoryIds))
                .fetchOne();

        if (groupIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total != null ? total : 0);
        }

        // ID 조회 결과에 해당하는 그룹 본문만 다시 조회. 연관 카테고리는 별도 Map 쿼리에서 붙임
        List<Group> groups = queryFactory
                .selectFrom(group)
                .distinct()
                .where(group.id.in(groupIds), accessibleGroup(memberId))
                .orderBy(orderSpecifiers)
                .fetch();

        return new PageImpl<>(groups, pageable, total != null ? total : 0);
    }

    public Page<Group> findMyGroupList(Long memberId, Pageable pageable) {
        OrderSpecifier<?>[] orderSpecifiers = toOrderSpecifiers(pageable.getSort());

        List<Long> groupIds = queryFactory
                .select(group.id)
                .from(groupMember)
                .join(groupMember.group, group)
                .where(groupMember.member.id.eq(memberId))
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(groupMember.id.count())
                .from(groupMember)
                .where(groupMember.member.id.eq(memberId))
                .fetchOne();

        if (groupIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total != null ? total : 0);
        }

        List<Group> groups = queryFactory
                .selectFrom(group)
                .where(group.id.in(groupIds))
                .orderBy(orderSpecifiers)
                .fetch();

        return new PageImpl<>(groups, pageable, total != null ? total : 0);
    }

    public Page<Group> findGroupListByName(Long memberId, String keyword, Pageable pageable) {
        OrderSpecifier<?>[] orderSpecifiers = toOrderSpecifiers(pageable.getSort());
        BooleanExpression nameCondition = normalizedNameContains(keyword);

        List<Long> groupIds = queryFactory
                .select(group.id)
                .from(group)
                .where(accessibleGroup(memberId), nameCondition)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(group.id.count())
                .from(group)
                .where(accessibleGroup(memberId), nameCondition)
                .fetchOne();

        if (groupIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total != null ? total : 0);
        }

        List<Group> groups = queryFactory
                .selectFrom(group)
                .where(group.id.in(groupIds), accessibleGroup(memberId))
                .orderBy(orderSpecifiers)
                .fetch();

        return new PageImpl<>(groups, pageable, total != null ? total : 0);
    }

    public Optional<Group> findAccessibleGroupDetailById(Long groupId, Long memberId) {
        Group foundGroup = queryFactory
                .selectFrom(group)
                .leftJoin(group.member, member).fetchJoin()
                .where(group.id.eq(groupId), accessibleGroup(memberId))
                .fetchOne();

        return Optional.ofNullable(foundGroup);
    }

    public List<Comment> findCommentsByGroupId(Long groupId) {
        // 댓글 작성자를 fetch join해서 작성자 정보 접근 시 추가 쿼리가 나가지 않게 한다.
        return queryFactory
                .selectFrom(comment)
                .leftJoin(comment.member, member).fetchJoin()
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.group.id.eq(groupId))
                .orderBy(comment.createdAt.asc(), comment.id.asc())
                .fetch();
    }

    public Map<Long, List<Category>> findCategoriesByGroupIds(List<Long> groupIds) {
        // 목록에 표시할 카테고리를 groupId 기준으로 한 번에 조회해 DTO 조립용 Map으로 변환
        if (groupIds == null || groupIds.isEmpty()) {
            return Map.of();
        }

        List<Tuple> rows = queryFactory
                .select(groupCategory.group.id, category)
                .from(groupCategory)
                .join(groupCategory.category, category)
                .where(groupCategory.group.id.in(groupIds))
                .fetch();

        Map<Long, List<Category>> categoriesByGroupId = new LinkedHashMap<>();
        rows.forEach(row -> {
            Long groupId = row.get(groupCategory.group.id);
            Category selectedCategory = row.get(category);
            categoriesByGroupId.computeIfAbsent(groupId, ignored -> new ArrayList<>())
                    .add(selectedCategory);
        });

        return categoriesByGroupId;
    }

    public Map<Long, Integer> findCurrentMemberCountsByGroupIds(List<Long> groupIds) {
        // 목록에 표시할 현재 참여 인원 수를 groupId 기준으로 한 번에 집계
        if (groupIds == null || groupIds.isEmpty()) {
            return Map.of();
        }

        List<Tuple> rows = queryFactory
                .select(groupMember.group.id, groupMember.id.count())
                .from(groupMember)
                .where(groupMember.group.id.in(groupIds))
                .groupBy(groupMember.group.id)
                .fetch();

        Map<Long, Integer> currentMemberCountByGroupId = new LinkedHashMap<>();
        rows.forEach(row -> {
            Long groupId = row.get(groupMember.group.id);
            Long currentMemberCount = row.get(groupMember.id.count());
            currentMemberCountByGroupId.put(groupId, currentMemberCount.intValue());
        });

        return currentMemberCountByGroupId;
    }

    public Set<Long> findGroupMemberIdsByGroupId(Long groupId) {
        return queryFactory
                .select(groupMember.member.id)
                .from(groupMember)
                .where(groupMember.group.id.eq(groupId))
                .fetch()
                .stream()
                .collect(Collectors.toSet());
    }

    private BooleanExpression categoryIdIn(List<Long> categoryIds) {
        // categoryIds가 없으면 where 조건을 만들지 않아 전체 조회됨
        if (categoryIds == null || categoryIds.isEmpty()) {
            return null;
        }

        return groupCategory.category.id.in(categoryIds);
    }

    private BooleanExpression normalizedNameContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        String normalizedKeyword = keyword.replaceAll("\\s+", "");
        if (normalizedKeyword.isBlank()) {
            return null;
        }

        BooleanExpression nameMatch = Expressions.stringTemplate("replace({0}, ' ', '')", group.name)
                .containsIgnoreCase(normalizedKeyword);
        BooleanExpression titleMatch = Expressions.stringTemplate("replace({0}, ' ', '')", group.title)
                .containsIgnoreCase(normalizedKeyword);
        return nameMatch.or(titleMatch);
    }

    private BooleanExpression accessibleGroup(Long memberId) {
        return group.isPublic.isTrue()
                .or(group.member.id.eq(memberId));
    }

    private OrderSpecifier<?>[] toOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (sort == null || sort.isUnsorted()) {
            orderSpecifiers.add(group.updatedAt.desc());
        } else {
            sort.forEach(order -> {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                OrderSpecifier<?> orderSpecifier = toOrderSpecifier(order.getProperty(), direction);

                if (orderSpecifier != null) {
                    orderSpecifiers.add(orderSpecifier);
                }
            });
        }

        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(group.updatedAt.desc());
        }

        orderSpecifiers.add(group.id.desc());
        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }

    private OrderSpecifier<?> toOrderSpecifier(String property, Order direction) {
        return switch (property) {
            case "createdAt" -> new OrderSpecifier<>(direction, group.createdAt);
            case "updatedAt" -> new OrderSpecifier<>(direction, group.updatedAt);
            case "name" -> new OrderSpecifier<>(direction, group.name);
            case "title" -> new OrderSpecifier<>(direction, group.title);
            case "capacity" -> new OrderSpecifier<>(direction, group.capacity);
            case "id" -> new OrderSpecifier<>(direction, group.id);
            default -> null;
        };
    }
}
