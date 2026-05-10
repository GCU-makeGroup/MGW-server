package com.awp.mgw.group.controller.dto.response;

import com.awp.mgw.category.domain.Category;
import com.awp.mgw.group.domain.Group;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// 전체 응답을 감싸는 Wrapper DTO (페이징 정보 + 그룹 리스트)
public record GetGroupListResponse(
        PageInfo pageInfo,
        List<GroupListItemResponse> groups
) {

    // Service 계층에서 넘겨받은 DB 조회 결과(Page), 댓글 수(Map), 카테고리(Map)를 하나로 조립하는 팩토리 메서드
    public static GetGroupListResponse from(
            Page<Group> groupPage,
            Map<Long, Integer> commentCountByGroupId,
            Map<Long, Integer> currentMemberCountByGroupId,
            Map<Long, List<Category>> categoriesByGroupId
    ) {
        return new GetGroupListResponse(
                PageInfo.from(groupPage),
                groupPage.getContent().stream()
                        .map(group -> GroupListItemResponse.from(
                                group,
                                // N+1 쿼리 방지를 위해 밖에서 미리 세팅해 온 Map에서 꺼내 씀 (없으면 0)
                                commentCountByGroupId.getOrDefault(group.getId(), 0),
                                currentMemberCountByGroupId.getOrDefault(group.getId(), 0),
                                categoriesByGroupId.getOrDefault(group.getId(), List.of())
                        ))
                        .toList()
        );
    }

    public record PageInfo(
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean hasNext
    ) {
        public static PageInfo from(Page<?> page) {
            return new PageInfo(
                    page.getNumber() + 1, // 1-indexed 처리
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.hasNext()
            );
        }
    }

    // 리스트 배열 안에 들어갈 개별 그룹(게시글)
    public record GroupListItemResponse(
            Long id,
            LocalDateTime updatedAt,
            String name,
            String title,
            List<CategoryInfo> categories,
            Integer capacity,
            Integer currentMemberCount,
            Integer commentCount
    ) {
        public static GroupListItemResponse from(
                Group group,
                Integer commentCount,
                Integer currentMemberCount,
                List<Category> categories
        ) {
            return new GroupListItemResponse(
                    group.getId(),
                    group.getUpdatedAt(),
                    group.getName(),
                    group.getTitle(),
                    categories.stream()
                            .map(CategoryInfo::from)
                            .toList(),
                    group.getCapacity(),
                    currentMemberCount,
                    commentCount
            );
        }
    }

    // 카테고리 정보
    public record CategoryInfo(Long id, String name) {
        public static CategoryInfo from(Category category) {
            return new CategoryInfo(category.getId(), category.getName());
        }
    }
}
