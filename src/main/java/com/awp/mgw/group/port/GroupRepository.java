package com.awp.mgw.group.port;

import com.awp.mgw.group.domain.Group;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 과제 요구사항에 맞춰 모든 DB 접근을 native query로만 수행합니다.
 */
public interface GroupRepository extends Repository<Group, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        INSERT INTO `group` (title, content, is_public, created_at, updated_at)
        VALUES (:title, :content, :isPublic, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6))
        """, nativeQuery = true)
    int insert(
        @Param("title") String title,
        @Param("content") String content,
        @Param("isPublic") boolean isPublic
    );

    @Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Long findLastInsertId();

    @Query(value = """
        SELECT
            g.id AS id,
            g.title AS title,
            g.content AS content,
            g.is_public AS publicGroup,
            g.created_at AS createdAt,
            g.updated_at AS updatedAt,
            COUNT(gm.id) AS memberCount
        FROM `group` g
        LEFT JOIN group_member gm ON gm.group_id = g.id
        WHERE g.id = :groupId
        GROUP BY g.id, g.title, g.content, g.is_public, g.created_at, g.updated_at
        """, nativeQuery = true)
    Optional<GroupSummaryProjection> findSummaryById(@Param("groupId") Long groupId);

    @Query(value = """
        SELECT
            g.id AS id,
            g.title AS title,
            g.content AS content,
            g.is_public AS publicGroup,
            g.created_at AS createdAt,
            g.updated_at AS updatedAt,
            COUNT(gm.id) AS memberCount
        FROM `group` g
        LEFT JOIN group_member gm ON gm.group_id = g.id
        WHERE (:isPublic IS NULL OR g.is_public = :isPublic)
        GROUP BY g.id, g.title, g.content, g.is_public, g.created_at, g.updated_at
        ORDER BY g.id DESC
        """, nativeQuery = true)
    List<GroupSummaryProjection> findAllSummaries(@Param("isPublic") Boolean isPublic);

    @Query(value = "SELECT COUNT(*) FROM `group` WHERE id = :groupId", nativeQuery = true)
    int countById(@Param("groupId") Long groupId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE `group`
        SET title = :title,
            content = :content,
            is_public = :isPublic,
            updated_at = CURRENT_TIMESTAMP(6)
        WHERE id = :groupId
        """, nativeQuery = true)
    int update(
        @Param("groupId") Long groupId,
        @Param("title") String title,
        @Param("content") String content,
        @Param("isPublic") boolean isPublic
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM `group` WHERE id = :groupId", nativeQuery = true)
    int deleteById(@Param("groupId") Long groupId);
}
