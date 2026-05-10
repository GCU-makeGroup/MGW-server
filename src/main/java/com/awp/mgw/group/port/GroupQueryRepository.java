package com.awp.mgw.group.port;

import com.awp.mgw.category.domain.Category;
import com.awp.mgw.group.domain.Group;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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

import static com.awp.mgw.category.domain.QCategory.category;
import static com.awp.mgw.group.domain.QGroup.group;
import static com.awp.mgw.group.domain.QGroupCategory.groupCategory;
import static com.awp.mgw.group.domain.QGroupMember.groupMember;

/**
 * 동적 쿼리를 위한 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class GroupQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Group> findGroupList(List<Long> categoryIds, Pageable pageable) {
        OrderSpecifier<?>[] orderSpecifiers = toOrderSpecifiers(pageable.getSort());

        // 컬렉션 조인을 바로 페이징하지 않기 위해, 먼저 조건에 맞는 그룹 ID만 페이지 단위로 조회
        List<Long> groupIds = queryFactory
                .select(group.id)
                .from(group)
                .leftJoin(group.groupCategories, groupCategory)
                .where(categoryIdIn(categoryIds))
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
                .where(categoryIdIn(categoryIds))
                .fetchOne();

        if (groupIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total != null ? total : 0);
        }

        // ID 조회 결과에 해당하는 그룹 본문만 다시 조회. 연관 카테고리는 별도 Map 쿼리에서 붙임
        List<Group> groups = queryFactory
                .selectFrom(group)
                .distinct()
                .where(group.id.in(groupIds))
                .orderBy(orderSpecifiers)
                .fetch();

        return new PageImpl<>(groups, pageable, total != null ? total : 0);
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

    private BooleanExpression categoryIdIn(List<Long> categoryIds) {
        // categoryIds가 없으면 where 조건을 만들지 않아 전체 조회됨
        if (categoryIds == null || categoryIds.isEmpty()) {
            return null;
        }

        return groupCategory.category.id.in(categoryIds);
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
