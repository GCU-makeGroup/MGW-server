package com.awp.mgw.group.port;

import com.awp.mgw.group.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 그룹 목록 화면에서 필요한 댓글 수만 groupId별로 받기 위한 Projection
    interface CommentCountProjection {
        Long getGroupId();
        Long getCommentCount();
    }

    // 목록에 포함된 그룹들의 댓글 수를 한 번에 집계해서 댓글 개수 조회 N+1을 방지
    @Query("SELECT c.group.id AS groupId, COUNT(c) AS commentCount " +
            "FROM Comment c " +
            "WHERE c.group.id IN :groupIds " +
            "GROUP BY c.group.id")
    List<CommentCountProjection> countCommentsByGroupIds(@Param("groupIds") List<Long> groupIds);
}
