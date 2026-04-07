package com.awp.mgw.member.port;

import com.awp.mgw.member.domain.Member;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 과제 요구사항에 맞춰 모든 CRUD를 native query로 노출합니다.
 */
public interface MemberRepository extends Repository<Member, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        INSERT INTO member (email, name, image_url, introduction, point, created_at, updated_at)
        VALUES (:email, :name, :imageUrl, :introduction, :point, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6))
        """, nativeQuery = true)
    int insert(
        @Param("email") Long email,
        @Param("name") String name,
        @Param("imageUrl") String imageUrl,
        @Param("introduction") String introduction,
        @Param("point") Integer point
    );

    @Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Long findLastInsertId();

    @Query(value = """
        SELECT *
        FROM member
        WHERE id = :memberId
          AND deleted_at IS NULL
        """, nativeQuery = true)
    Optional<Member> findActiveById(@Param("memberId") Long memberId);

    @Query(value = """
        SELECT *
        FROM member
        WHERE deleted_at IS NULL
        ORDER BY id DESC
        """, nativeQuery = true)
    List<Member> findAllActive();

    @Query(value = """
        SELECT COUNT(*)
        FROM member
        WHERE id = :memberId
          AND deleted_at IS NULL
        """, nativeQuery = true)
    int countActiveById(@Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE member
        SET name = :name,
            image_url = :imageUrl,
            introduction = :introduction,
            point = :point,
            updated_at = CURRENT_TIMESTAMP(6)
        WHERE id = :memberId
          AND deleted_at IS NULL
        """, nativeQuery = true)
    int update(
        @Param("memberId") Long memberId,
        @Param("name") String name,
        @Param("imageUrl") String imageUrl,
        @Param("introduction") String introduction,
        @Param("point") Integer point
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE member
        SET deleted_at = CURRENT_TIMESTAMP(6),
            updated_at = CURRENT_TIMESTAMP(6)
        WHERE id = :memberId
          AND deleted_at IS NULL
        """, nativeQuery = true)
    int softDelete(@Param("memberId") Long memberId);
}
