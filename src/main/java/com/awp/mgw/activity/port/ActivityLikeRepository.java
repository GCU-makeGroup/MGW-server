package com.awp.mgw.activity.port;

import com.awp.mgw.activity.domain.Activity;
import com.awp.mgw.activity.domain.ActivityLike;
import com.awp.mgw.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityLikeRepository extends JpaRepository<ActivityLike, Long> {
    boolean existsByMemberAndActivity(Member member, Activity activity);
    Optional<ActivityLike> findByMemberAndActivity(Member member, Activity activity);
}
