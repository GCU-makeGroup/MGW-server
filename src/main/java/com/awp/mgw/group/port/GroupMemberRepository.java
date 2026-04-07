package com.awp.mgw.group.port;

import com.awp.mgw.group.domain.GroupMember;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupMemberRepository extends Repository<GroupMember, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        INSERT INTO group_member (group_id, member_id)
        VALUES (:groupId, :memberId)
        """, nativeQuery = true)
    int insert(@Param("groupId") Long groupId, @Param("memberId") Long memberId);

    @Query(value = """
        SELECT
            gm.id AS groupMemberId,
            m.id AS memberId,
            m.email AS email,
            m.name AS name,
            m.image_url AS imageUrl,
            m.introduction AS introduction,
            m.point AS point
        FROM group_member gm
        INNER JOIN member m ON m.id = gm.member_id
        WHERE gm.group_id = :groupId
          AND m.deleted_at IS NULL
        ORDER BY gm.id DESC
        """, nativeQuery = true)
    List<GroupMemberProjection> findMembersByGroupId(@Param("groupId") Long groupId);

    @Query(value = """
        SELECT COUNT(*)
        FROM group_member
        WHERE group_id = :groupId
          AND member_id = :memberId
        """, nativeQuery = true)
    int countByGroupIdAndMemberId(@Param("groupId") Long groupId, @Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        DELETE FROM group_member
        WHERE group_id = :groupId
          AND member_id = :memberId
        """, nativeQuery = true)
    int delete(@Param("groupId") Long groupId, @Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM group_member WHERE group_id = :groupId", nativeQuery = true)
    int deleteAllByGroupId(@Param("groupId") Long groupId);
}
