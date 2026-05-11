package com.awp.mgw.activity.port;

import com.awp.mgw.activity.domain.Activity;
import com.awp.mgw.activity.domain.ActivityGroup;
import com.awp.mgw.activity.domain.enums.ActivityGroupStatus;
import com.awp.mgw.group.domain.Group;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface ActivityGroupRepository extends JpaRepository<ActivityGroup, Long> {
    boolean existsByActivityAndGroupAndStatusIn(Activity activity, Group group, Iterable<ActivityGroupStatus> statuses);
    void deleteAllByGroup(Group group);

    @Query("""
        select count(ag) > 0
        from ActivityGroup ag
        join ag.group g
        join g.groupMembers gm
        where ag.activity.id = :activityId
          and gm.member.id = :memberId
          and ag.status in :statuses
    """)
    boolean existsByActivityIdAndMemberIdAndStatusIn(
        @Param("activityId") Long activityId,
        @Param("memberId") Long memberId,
        @Param("statuses") Collection<ActivityGroupStatus> statuses
    );

    @Modifying
    @Query("""
        delete from ActivityGroup ag
        where ag.activity.id = :activityId
          and ag.status in :statuses
          and ag.group.id in (
            select gm.group.id
            from GroupMember gm
            where gm.member.id = :memberId
          )
    """)
    int deleteByActivityIdAndMemberIdAndStatusIn(
        @Param("activityId") Long activityId,
        @Param("memberId") Long memberId,
        @Param("statuses") Collection<ActivityGroupStatus> statuses
    );
}
